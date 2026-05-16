package vista.panel;

import controlador.ParqueoService;
import modelo.parqueo.EspacioParqueo;
import modelo.parqueo.EspacioParqueo.EstadoEspacio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class MapaPanel extends JPanel {

    // ── Paleta ────────────────────────────────────────────────
    private static final Color BG          = new Color(18, 24, 40);
    private static final Color COLOR_LIBRE = new Color(34, 197, 94);
    private static final Color COLOR_OCUP  = new Color(239, 68, 68);
    private static final Color COLOR_DISC  = new Color(59, 130, 246);
    private static final Color TEXT_MUTED  = new Color(148, 163, 184);
    private static final Color HEADER_BG   = new Color(30, 41, 59);

    private final ParqueoService service;
    private Consumer<EspacioParqueo> onEspacioSeleccionado;

    public MapaPanel(ParqueoService service) {
        this.service = service;
        setBackground(BG);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        buildUI();
    }

    public void setOnEspacioSeleccionado(Consumer<EspacioParqueo> handler) {
        this.onEspacioSeleccionado = handler;
    }

    private void buildUI() {
        add(buildLeyenda(), BorderLayout.NORTH);
        add(buildGrilla(), BorderLayout.CENTER);
    }

    
    private JPanel buildLeyenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        p.setBackground(BG);
        p.add(leyendaItem(COLOR_LIBRE, "Libre"));
        p.add(leyendaItem(COLOR_OCUP,  "Ocupado"));
        p.add(leyendaItem(COLOR_DISC,  "Discapacidad"));
        return p;
    }

    private JPanel leyendaItem(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setBackground(BG);
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 2, 12, 12);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(14, 16));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        p.add(dot);
        p.add(lbl);
        return p;
    }

    
    public JPanel buildGrilla() {
        EspacioParqueo[][] mapa = service.getMapa();
        int filas = mapa.length;
        int cols  = mapa[0].length;

        JPanel contenedor = new JPanel(new BorderLayout(0, 4));
        contenedor.setBackground(BG);

        
        JPanel headerCols = new JPanel(new GridLayout(1, cols + 1, 6, 0));
        headerCols.setBackground(BG);
        headerCols.add(emptyCell());
        for (int c = 1; c <= cols; c++) {
            headerCols.add(headerCell(String.valueOf(c)));
        }
        contenedor.add(headerCols, BorderLayout.NORTH);

        
        JPanel grid = new JPanel(new GridLayout(filas, cols + 1, 6, 6));
        grid.setBackground(BG);

        char[] letras = {'A', 'B', 'C'};
        for (int f = 0; f < filas; f++) {
            grid.add(headerCell(String.valueOf(letras[f])));
            for (int c = 0; c < cols; c++) {
                grid.add(buildEspacioCell(mapa[f][c]));
            }
        }
        contenedor.add(grid, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel buildEspacioCell(EspacioParqueo espacio) {
        JPanel cell = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = esDiscapacidadOcupado(espacio)
                        ? new Color(96, 165, 250)
                        : colorPorEstado(espacio.getEstado());
                
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Borde coloreado
                g2.setColor(bg);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

                g2.dispose();
            }
        };
        cell.setOpaque(false);
        cell.setPreferredSize(new Dimension(70, 60));
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ID del espacio
        JLabel idLabel = new JLabel(espacio.getId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(esDiscapacidadOcupado(espacio)
                ? new Color(96, 165, 250)
                : colorPorEstado(espacio.getEstado()));

        // Ícono de estado
        JLabel iconLabel = new JLabel(iconoPorEstado(espacio.getEstado()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        idLabel.setAlignmentX(CENTER_ALIGNMENT);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(idLabel);
        inner.add(iconLabel);

        cell.add(inner);

        
        String tooltip = buildTooltip(espacio);
        cell.setToolTipText(tooltip);

        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onEspacioSeleccionado != null) {
                    onEspacioSeleccionado.accept(espacio);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(new Color(255, 255, 255, 10));
                cell.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cell.repaint();
            }
        });

        return cell;
    }

    private JPanel headerCell(String texto) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(HEADER_BG);
        p.setPreferredSize(new Dimension(70, 32));
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT_MUTED);
        p.add(l);
        return p;
    }

    private JPanel emptyCell() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setPreferredSize(new Dimension(32, 32));
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────
    public static Color colorPorEstado(EstadoEspacio espacio) {
        return switch (espacio) {
            case LIBRE         -> COLOR_LIBRE;
            case OCUPADO       -> COLOR_OCUP;
            case DISCAPACIDAD  -> COLOR_DISC;
        };
    }

    private String iconoPorEstado(EstadoEspacio estado) {
        return switch (estado) {
            case LIBRE        -> "○";
            case OCUPADO      -> "🚗";
            case DISCAPACIDAD -> "♿";
        };
    }

    private String buildTooltip(EspacioParqueo e) {
        if (e.getVehiculoActual() != null) {
            return "<html><b>" + e.getId() + "</b><br>Espacio ocupado<br>Placa:  " +
                e.getVehiculoActual().getPlaca() + "<br>Propietario: " +
                e.getVehiculoActual().getPropietario() + "</html>";
        }
        return "<html><b>" + e.getId() + "</b><br>Estado: " +
            e.getEstado() + "</html>";
    }
    
    private boolean esDiscapacidadOcupado(EspacioParqueo espacio) {
    return espacio.getEstado() == EstadoEspacio.OCUPADO
        && espacio.isEsDiscapacidad();
}
    
    public void refrescar() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}
