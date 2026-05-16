package vista.panel;

import controlador.ParqueoService;
import modelo.Exception.*;
import modelo.enums.tipoVehiculo;
import modelo.parqueo.EspacioParqueo;
import modelo.parqueo.Vehiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RegistroDialog extends JDialog {

    private static final Color BG         = new Color(18, 24, 40);
    private static final Color CARD_BG    = new Color(24, 32, 52);
    private static final Color ACCENT     = new Color(59, 130, 246);
    private static final Color SUCCESS    = new Color(34, 197, 94);
    private static final Color DANGER     = new Color(239, 68, 68);
    private static final Color TEXT_WHITE = new Color(240, 245, 255);
    private static final Color BLACK    = new Color(0, 0, 0);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color FIELD_BG   = new Color(30, 41, 59);
    private static final Color BORDER_CLR = new Color(51, 65, 85);

    private final ParqueoService service;
    private final Runnable onCambio;
    private EspacioParqueo espacioPreseleccionado;

    // Entrada
    private JTextField entPlaca, entPropietario;
    private JComboBox<tipoVehiculo> entTipo;
    private JComboBox<String> entEspacio;
    private JLabel entMensaje;

    // Salida
    private JTextField salPlaca;
    private JLabel salInfo, salMensaje;

    public RegistroDialog(Frame owner, ParqueoService service, Runnable onCambio) {
        super(owner, "Registro de Vehículo", true);
        this.service = service;
        this.onCambio = onCambio;
        setSize(480, 420);
        setLocationRelativeTo(owner);
        setResizable(false);
        buildUI();
    }

    public void preseleccionarEspacio(EspacioParqueo espacio) {
        this.espacioPreseleccionado = espacio;
        if (entEspacio != null) {
            for (int i = 0; i < entEspacio.getItemCount(); i++) {
                if (entEspacio.getItemAt(i).equals(espacio.getId())) {
                    entEspacio.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD_BG);
        tabs.setForeground(TEXT_WHITE);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = CARD_BG;
                lightHighlight = CARD_BG;
                shadow = BORDER_CLR;
                darkShadow = BORDER_CLR;
                focus = ACCENT;
            }
        });

        tabs.addTab("  Entrada  ", buildTabEntrada());
        tabs.addTab("  Salida   ", buildTabSalida());
        tabs.setBackgroundAt(0, CARD_BG);
        tabs.setBackgroundAt(1, CARD_BG);

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
        getContentPane().setBackground(BG);
    }

    // ── Tab Entrada ───────────────────────────────────────────
    private JPanel buildTabEntrada() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        entPlaca      = styledField();
        entPropietario= styledField();
        entTipo       = new JComboBox<>(tipoVehiculo.values());
        styleCombo(entTipo);

        // Espacios libres
        entEspacio = new JComboBox<>();
        entEspacio.addItem("-- Asignar automáticamente --");
        List<EspacioParqueo> libres = service.getEspaciosLibres();
        for (EspacioParqueo e : libres) {
            if (e.getEstado() == EspacioParqueo.EstadoEspacio.LIBRE || e.getEstado() == EspacioParqueo.EstadoEspacio.DISCAPACIDAD) {
                entEspacio.addItem(e.getId());
            }
        }
        styleCombo(entEspacio);

        entMensaje = new JLabel(" ");
        entMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        entMensaje.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnRegistrar = accionBtn("Registrar Entrada", ACCENT);
        btnRegistrar.addActionListener(e -> registrarEntrada());

        p.add(fieldRow("Placa", entPlaca));
        p.add(Box.createVerticalStrut(12));
        p.add(fieldRow("Propietario", entPropietario));
        p.add(Box.createVerticalStrut(12));
        p.add(fieldRow("Tipo de Vehículo", entTipo));
        p.add(Box.createVerticalStrut(12));
        p.add(fieldRow("Espacio", entEspacio));
        p.add(Box.createVerticalStrut(14));
        p.add(entMensaje);
        p.add(Box.createVerticalStrut(10));
        p.add(btnRegistrar);

        if (espacioPreseleccionado != null) preseleccionarEspacio(espacioPreseleccionado);

        return p;
    }

    private void registrarEntrada() {
        String placa = entPlaca.getText().trim();
        String prop  = entPropietario.getText().trim();
        tipoVehiculo tipo = (tipoVehiculo) entTipo.getSelectedItem();
        String esp   = entEspacio.getSelectedIndex() == 0 ? null
                      : (String) entEspacio.getSelectedItem();

        if (placa.isEmpty() || prop.isEmpty()) {
            mostrarMsg(entMensaje, "Complete placa y propietario.", DANGER);
            return;
        }

        try {
            Vehiculo v = service.registrarEntrada(placa, tipo, prop, esp);
            mostrarMsg(entMensaje,
                "✅ Entrada registrada → Espacio " + v.getEspacioId(), SUCCESS);
            entPlaca.setText("");
            entPropietario.setText("");
            if (onCambio != null) onCambio.run();
        } catch (SesionNoAutorizadaException ex) {
            mostrarMsg(entMensaje, "Sin permiso: " + ex.getMessage(), DANGER);
        } catch (EspacioNoDisponibleException ex) {
            mostrarMsg(entMensaje, "Espacio no disponible: " + ex.getMessage(), DANGER);
        } catch (VehiculoYaRegistradoException ex) {
            mostrarMsg(entMensaje, ex.getMessage(), DANGER);
        }
    }

    // ── Tab Salida ────────────────────────────────────────────
    private JPanel buildTabSalida() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        salPlaca = styledField();
        salInfo  = new JLabel(" ");
        salInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salInfo.setForeground(TEXT_MUTED);
        salInfo.setAlignmentX(LEFT_ALIGNMENT);

        salMensaje = new JLabel(" ");
        salMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salMensaje.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnBuscar = accionBtn("Buscar vehículo", new Color(30, 41, 59));
        btnBuscar.addActionListener(e -> {
            String pl = salPlaca.getText().trim();
            if (pl.isEmpty()) return;
            List<Vehiculo> res = service.buscar(pl);
            if (res.isEmpty()) {
                salInfo.setText("No encontrado: " + pl);
                salInfo.setForeground(DANGER);
            } else {
                Vehiculo v = res.get(0);
                salInfo.setText("<html>Propietario: <b>" + v.getPropietario() +
                    "</b>  |  Espacio: <b>" + v.getEspacioId() +
                    "</b>  |  Entrada: " + v.getHoraEntradaFormateada() + "</html>");
                salInfo.setForeground(TEXT_MUTED);
            }
        });

        JButton btnSalida = accionBtn("Registrar Salida", DANGER);
        btnSalida.addActionListener(e -> registrarSalida());

        p.add(fieldRow("Placa del Vehículo", salPlaca));
        p.add(Box.createVerticalStrut(10));
        p.add(btnBuscar);
        p.add(Box.createVerticalStrut(14));
        p.add(salInfo);
        p.add(Box.createVerticalStrut(14));
        p.add(salMensaje);
        p.add(Box.createVerticalStrut(10));
        p.add(btnSalida);

        return p;
    }

    private void registrarSalida() {
        String placa = salPlaca.getText().trim();
        if (placa.isEmpty()) { mostrarMsg(salMensaje, "Ingrese una placa.", DANGER); return; }
        try {
            Vehiculo v = service.registrarSalida(placa);
            mostrarMsg(salMensaje,
                "✅ Salida registrada para " + v.getPlaca() + " a las " + v.getHoraSalidaFormateada(),
                SUCCESS);
            salPlaca.setText("");
            salInfo.setText(" ");
            if (onCambio != null) onCambio.run();
        } catch (SesionNoAutorizadaException ex) {
            mostrarMsg(salMensaje, "Sin permiso: " + ex.getMessage(), DANGER);
        } catch (TicketNoEncontradoException ex) {
            mostrarMsg(salMensaje, ex.getMessage(), DANGER);
        } catch (CobroNoPermitidoException ex) {
            mostrarMsg(salMensaje, ex.getMessage(), DANGER);
        }
    }

    
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_WHITE);
        f.setBackground(FIELD_BG);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private <T> void styleCombo(JComboBox<T> cb) {
        cb.setBackground(FIELD_BG);
        cb.setForeground(BLACK);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                setBackground(sel ? new Color(59, 130, 246, 80) : FIELD_BG);
                setForeground(TEXT_WHITE);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
    }

    private JPanel fieldRow(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    private JButton accionBtn(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? color.darker()
                        : color;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.BLACK);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void mostrarMsg(JLabel label, String txt, Color color) {
        label.setText("<html>" + txt + "</html>");
        label.setForeground(color);
    }
}
