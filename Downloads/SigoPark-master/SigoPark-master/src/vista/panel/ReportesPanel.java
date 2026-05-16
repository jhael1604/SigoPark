package vista.panel;

import controlador.ParqueoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportesPanel extends JPanel {

    private static final Color BG         = new Color(18, 24, 40);
    private static final Color TEXT_WHITE  = new Color(240, 245, 255);
    private static final Color TEXT_MUTED  = new Color(148, 163, 184);

    private final ParqueoService service;

    // Referencias para actualizar

    public ReportesPanel(ParqueoService service) {
        this.service = service;
        setBackground(BG);
        setLayout(new BorderLayout(0, 24));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("Resumen del Parqueo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT_WHITE);
        titulo.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setBackground(BG);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Ocupados
        JPanel[] cards = new JPanel[4];
        cards[0] = buildCard("Espacios Ocupados",
            String.valueOf(service.espaciosOcupados()),
            new Color(239, 68, 68),
            new Color(239, 68, 68, 20), "🚗");
        cards[1] = buildCard("Espacios Libres",
            String.valueOf(service.espaciosLibres()),
            new Color(34, 197, 94),
            new Color(34, 197, 94, 20), "✅");
        cards[2] = buildCard("Espacios discapacitados",
            String.valueOf(service.espaciosDiscapacidad()),
            new Color(59, 130, 246),
            new Color(59, 130, 246, 20), "♿");
        cards[3] = buildCard("Total Espacios",
            String.valueOf(service.totalEspacios()),
            new Color(255,209,0),
            new Color(59, 130, 246, 20), "🅿");

        for (JPanel c : cards) cardsRow.add(c);
        add(cardsRow, BorderLayout.CENTER);

        // Segunda fila: distribución porcentual
        add(buildBarraDistribucion(), BorderLayout.SOUTH);
    }

    private JPanel buildCard(String titulo, String valor, Color accent, Color color1, String icono) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(24, 32, 52));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(51, 65, 85));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // Franja superior de color
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Icono
        JLabel ico = new JLabel(icono);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        ico.setAlignmentX(LEFT_ALIGNMENT);

        // Valor
        JLabel valLabel = new JLabel(valor);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valLabel.setForeground(accent);
        valLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Título
        JLabel titLabel = new JLabel(titulo);
        titLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titLabel.setForeground(TEXT_MUTED);
        titLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(ico);
        card.add(Box.createVerticalStrut(8));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(titLabel);

        return card;
    }

    private JPanel buildBarraDistribucion() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lbl = new JLabel("Distribución de ocupación");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_MUTED);
        wrapper.add(lbl, BorderLayout.NORTH);

        int total     = service.totalEspacios();
        int ocupados  = (int) service.espaciosOcupados();
        int libres    = (int) service.espaciosLibres();
        int discapacidad = (int)service.espaciosDiscapacidad();
                
        JPanel barra = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(30, 41, 59));
                g2.fillRoundRect(0, 0, w, h, h, h);

                int xOff = 0;
                int[] vals = {ocupados, libres, discapacidad, total - ocupados - libres - discapacidad};
                Color[] cols = {
                    new Color(239, 68, 68),
                    new Color(34, 197, 94),
                    new Color(59, 130, 246),
                    new Color(59, 130, 246),
                };
                for (int i = 0; i < vals.length; i++) {
                    int segW = total > 0 ? (int) ((double) vals[i] / total * w) : 0;
                    g2.setColor(cols[i]);
                    if (i == 0) g2.fillRoundRect(xOff, 0, segW, h, h, h);
                    else if (i == vals.length - 1) g2.fillRect(xOff, 0, w - xOff, h);
                    else g2.fillRect(xOff, 0, segW, h);
                    xOff += segW;
                }
                g2.dispose();
            }
        };
        barra.setPreferredSize(new Dimension(0, 14));
        barra.setOpaque(false);
        wrapper.add(barra, BorderLayout.CENTER);

        // Porcentajes
        JPanel pcts = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        pcts.setBackground(BG);
        String[] labels = {"Ocupados", "Libres", "Discapacidad"};
        Color[] cols = {new Color(239,68,68), new Color(34,197,94), new Color(59,130,246)};
        int[] vals = {ocupados, libres, (int)service.espaciosDiscapacidad()};
        for (int i = 0; i < labels.length; i++) {
            int pct = total > 0 ? (int)((double) vals[i] / total * 100) : 0;
            JLabel l = new JLabel("● " + labels[i] + ": " + pct + "%");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            l.setForeground(cols[i]);
            pcts.add(l);
        }
        wrapper.add(pcts, BorderLayout.SOUTH);
        return wrapper;
    }

    public void refrescar() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}
