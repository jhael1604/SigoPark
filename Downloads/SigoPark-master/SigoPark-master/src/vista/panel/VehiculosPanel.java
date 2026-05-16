    package vista.panel;

import controlador.ParqueoService;
import modelo.parqueo.Vehiculo;
import modelo.parqueo.Vehiculo.EstadoVehiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class VehiculosPanel extends JPanel {

    private static final Color BG         = new Color(18, 24, 40);
    private static final Color CARD_BG    = new Color(24, 32, 52);
    private static final Color ACCENT     = new Color(59, 130, 246);
    private static final Color TEXT_WHITE = new Color(240, 245, 255);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color FIELD_BG   = new Color(30, 41, 59);
    private static final Color BORDER_CLR = new Color(51, 65, 85);
    private static final Color ROW_ALT    = new Color(28, 38, 60);

    private final ParqueoService service;
    private DefaultTableModel tableModel;
    private JTextField txtFiltro;

    public VehiculosPanel(ParqueoService service) {
        this.service = service;
        setBackground(BG);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTabla(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(BG);

        JLabel titulo = new JLabel("Vehículos Estacionados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXT_WHITE);

        txtFiltro = new JTextField();
        txtFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltro.setForeground(TEXT_WHITE);
        txtFiltro.setBackground(FIELD_BG);
        txtFiltro.setCaretColor(ACCENT);
        txtFiltro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        txtFiltro.setPreferredSize(new Dimension(220, 34));
        JLabel hint = new JLabel("🔍");
        hint.setForeground(TEXT_MUTED);
        txtFiltro.setToolTipText("Filtrar por placa o propietario");
        txtFiltro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(BG);
        right.add(new JLabel("  🔍  ") {{ setForeground(TEXT_MUTED); }});
        right.add(txtFiltro);

        bar.add(titulo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTabla() {
        String[] cols = {"Placa", "Tipo", "Propietario", "Espacio", "Entrada", "Estado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                }
                return c;
            }
        };
        tabla.setBackground(CARD_BG);
        tabla.setForeground(TEXT_WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(59, 130, 246, 60));
        tabla.setSelectionForeground(TEXT_WHITE);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(30, 41, 59));
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        header.setReorderingAllowed(false);


        tabla.getColumnModel().getColumn(5).setCellRenderer(new BadgeRenderer());

        // Anchos
        int[] anchos = {100, 110, 180, 80, 140, 110};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(CARD_BG);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));

        cargarDatos(service.getVehiculosEstacionados());
        return scroll;
    }

    private void cargarDatos(List<Vehiculo> lista) {
        tableModel.setRowCount(0);
        for (Vehiculo v : lista) {
            tableModel.addRow(new Object[]{
                v.getPlaca(),
                v.getTipo().getDescripcion(),
                v.getPropietario(),
                v.getEspacioId(),
                v.getHoraEntradaFormateada(),
                v.getEstado()
            });
        }
    }

    public void refrescar() {
        filtrar();
    }

    private void filtrar() {
        String term = txtFiltro != null ? txtFiltro.getText().trim() : "";
        cargarDatos(service.buscar(term));
    }

    private static class BadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            JPanel badge = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 16, 12, 12);
                    g2.dispose();
                }
            };
            EstadoVehiculo estado = (EstadoVehiculo) val;
            Color bg; String txt;
            switch (estado) {
                case ESTACIONADO -> { bg = new Color(34, 197, 94, 40);  txt = "Estacionado"; }
                case RETIRADO    -> { bg = new Color(148, 163, 184, 40); txt = "Retirado"; }
                default          -> { bg = new Color(234, 179, 8, 40);  txt = "Pendiente"; }
            }
            badge.setBackground(bg);
            badge.setOpaque(false);
            JLabel lbl = new JLabel(txt);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(switch (estado) {
                case ESTACIONADO -> new Color(74, 222, 128);
                case RETIRADO    -> new Color(148, 163, 184);
                default          -> new Color(250, 204, 21);
            });
            badge.add(lbl);
            badge.setBackground(sel ? new Color(59, 130, 246, 30) : (row % 2 == 0
                    ? new Color(24, 32, 52) : new Color(28, 38, 60)));
            return badge;
        }
    }
}
