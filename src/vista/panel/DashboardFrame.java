package vista.panel;

import controlador.ParqueoService;
import modelo.parqueo.EspacioParqueo.EstadoEspacio;
import parking.vista.login.LoginFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private static final Color BG_DARK    = new Color(12, 17, 30);
    private static final Color SIDEBAR_BG = new Color(18, 24, 40);
    private static final Color ACCENT     = new Color(59, 130, 246);
    private static final Color TEXT_WHITE = new Color(240, 245, 255);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color ACTIVE_NAV = new Color(30, 58, 110);
    private static final Color BORDER_CLR = new Color(30, 41, 59);

    private final ParqueoService service;

    private JPanel contentArea;
    private MapaPanel mapaPanel;
    private VehiculosPanel vehiculosPanel;
    private ReportesPanel reportesPanel;

    private JButton btnNavActivo;

    public DashboardFrame(ParqueoService service) {
        this.service = service;
        setTitle("SigoPark");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(1100, 680));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
    }

   
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 5, BORDER_CLR));

        // Logo
        JPanel logoPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        logoPane.setBackground(SIDEBAR_BG);
        logoPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        logoPane.setMinimumSize(new Dimension(0, 64));
        JLabel logo = new JLabel("ParkSystem");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(TEXT_WHITE);
        logoPane.add(logo);
        sidebar.add(logoPane);
        sidebar.add(separador());

        // Navegación
        sidebar.add(Box.createVerticalStrut(8));
        JButton btnMapa      = navBtn("Mapa de Espacios");
        JButton btnVehiculos = navBtn("Vehículos registrardos");
        JButton btnReportes  = navBtn("Reportes");

        btnMapa.addActionListener(e      -> navegarA(mapaPanel, btnMapa));
        btnVehiculos.addActionListener(e -> navegarA(vehiculosPanel, btnVehiculos));
        btnReportes.addActionListener(e  -> navegarA(reportesPanel, btnReportes));

        sidebar.add(btnMapa);
        sidebar.add(btnVehiculos);
        sidebar.add(btnReportes);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(separador());

        // Usuario actual
        JPanel userPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        userPane.setBackground(SIDEBAR_BG);
        userPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        String nombreCompleto = service.getSesionActual() != null ? service.getSesionActual().getNombreCompleto() : "----";
        String numDoc = service.getSesionActual() != null ? service.getSesionActual().getNumeroDocumento() : "-----";
        JLabel userLabel = new JLabel("<html>" + nombreCompleto + "<br><span style='color: #94a3b8; font-size:10px'>Doc: " + numDoc + "</span>");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_MUTED);

        JButton btnLogout = new JButton("Salir");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnLogout.setForeground(new Color(239, 68, 68));
        btnLogout.setBackground(SIDEBAR_BG);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            service.cerrarSesion();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(service).setVisible(true));
        });

        userPane.add(userLabel);
        userPane.add(btnLogout);
        sidebar.add(userPane);

        // Activar primer ítem
        btnNavActivo = btnMapa;
        setActivo(btnMapa, true);

        return sidebar;
    }

    private JButton navBtn(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (this == btnNavActivo) {
                    g2.setColor(ACTIVE_NAV);
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                    // Barra izquierda
                    g2.setColor(ACCENT);
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 41, 59));
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(SIDEBAR_BG);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 18, 10, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setActivo(JButton btn, boolean activo) {
        btn.setForeground(activo ? TEXT_WHITE : TEXT_MUTED);
        btn.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 13));
    }

    private void navegarA(JPanel panel, JButton navBtn) {
        if (btnNavActivo != null) {
            setActivo(btnNavActivo, false);
            btnNavActivo.repaint();
        }
        btnNavActivo = navBtn;
        setActivo(navBtn, true);
        navBtn.repaint();
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        panel.repaint();
    }

    private JSeparator separador() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setForeground(BORDER_CLR);
        sep.setBackground(BORDER_CLR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Main area ─────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_DARK);
        main.add(buildTopBar(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_DARK);

        // Paneles
        mapaPanel      = new MapaPanel(service);
        vehiculosPanel = new VehiculosPanel(service);
        reportesPanel  = new ReportesPanel(service);

        // Clic en espacio del mapa → abrir dialog
        mapaPanel.setOnEspacioSeleccionado(espacio -> {
            if (espacio.getEstado() == EstadoEspacio.LIBRE ||
                espacio.getEstado() == EstadoEspacio.DISCAPACIDAD) {
                RegistroDialog dlg = new RegistroDialog(this, service, this::refrescarTodo);
                dlg.preseleccionarEspacio(espacio);
                dlg.setVisible(true);
            } else if (espacio.getEstado() == EstadoEspacio.OCUPADO) {
                int opt = JOptionPane.showConfirmDialog(
                    this,
                    "<html>Espacio <b>" + espacio.getId() + "</b> — " +
                    (espacio.getVehiculoActual() != null
                        ? espacio.getVehiculoActual().getPlaca() : "") +
                    "<br>¿Registrar salida?</html>",
                    "Registrar Salida",
                    JOptionPane.YES_NO_OPTION
                );
                if (opt == JOptionPane.YES_OPTION) {
                    RegistroDialog dlg = new RegistroDialog(this, service, this::refrescarTodo);
                    dlg.setVisible(true);
                }
            }
        });

        contentArea.add(mapaPanel, BorderLayout.CENTER);
        main.add(contentArea, BorderLayout.CENTER);
        return main;
    }

    // ── Top bar ───────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(10, 18, 10, 18)
        ));
        bar.setPreferredSize(new Dimension(0, 52));

        JLabel breadcrumb = new JLabel("SigoPark");
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumb.setForeground(TEXT_MUTED);

        JButton btnNuevo = buildAccentButton("+ Nuevo Registro");
        btnNuevo.addActionListener(e -> {
            RegistroDialog dlg = new RegistroDialog(this, service, this::refrescarTodo);
            dlg.setVisible(true);
        });

        bar.add(breadcrumb, BorderLayout.WEST);
        bar.add(btnNuevo, BorderLayout.EAST);
        return bar;
    }

    private JButton buildAccentButton(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(37, 99, 235) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Refresh ───────────────────────────────────────────────
    private void refrescarTodo() {
        mapaPanel.refrescar();
        vehiculosPanel.refrescar();
        reportesPanel.refrescar();
    }
    
}
