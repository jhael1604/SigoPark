package parking.vista.login;

import controlador.ParqueoService;
import modelo.Exception.*;

import modelo.usuarios.Usuario;
import vista.panel.DashboardFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    
    private static final Color BG_DARK     = new Color(15, 20, 35);
    private static final Color CARD_BG     = new Color(24, 32, 52);
    private static final Color ACCENT      = new Color(59, 130, 246);
    private static final Color ACCENT_HOV  = new Color(37, 99, 235);
    private static final Color TEXT_WHITE  = new Color(240, 245, 255);
    private static final Color TEXT_MUTED  = new Color(148, 163, 184);
    private static final Color FIELD_BG    = new Color(30, 41, 59);
    private static final Color FIELD_BORDER= new Color(51, 65, 85);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    private final ParqueoService service;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginFrame(ParqueoService service) {
        this.service = service;
        setTitle("Sistema de Gestión de Parqueos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 420, 520, 20, 20));
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                RadialGradientPaint glow = new RadialGradientPaint(
                    210, 80, 200,
                    new float[]{0f, 1f},
                    new Color[]{new Color(59, 130, 246, 40), new Color(15, 20, 35, 0)}
                );
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        
        addDragSupport(root);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(FIELD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 36, 36, 36));
        card.setPreferredSize(new Dimension(340, 420));

        // ── Logo / Icono ─────────────────────────────────────
        JLabel iconLabel = new JLabel("🅿", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        // ── Título ────────────────────────────────────────────
        JLabel title = styledLabel("ParkSystem", 22, Font.BOLD, TEXT_WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = styledLabel("Gestión de Parqueos", 13, Font.PLAIN, TEXT_MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // ── Campos ────────────────────────────────────────────
        txtUsuario = buildField("Usuario");
        txtPassword = new JPasswordField();
        styleField(txtPassword, "Contraseña");

        // ── Error label ───────────────────────────────────────
        lblError = styledLabel(" ", 12, Font.PLAIN, ERROR_COLOR);
        lblError.setAlignmentX(CENTER_ALIGNMENT);

        // ── Botón ─────────────────────────────────────────────
        JButton btnLogin = buildLoginButton();

        // ── Cerrar ────────────────────────────────────────────
        JButton btnClose = new JButton("✕");
        btnClose.setForeground(TEXT_MUTED);
        btnClose.setBackground(new Color(0, 0, 0, 0));
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));

       


        
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        closePanel.add(btnClose);

        card.add(closePanel);
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(fieldGroup("Usuario", txtUsuario));
        card.add(Box.createVerticalStrut(14));
        card.add(fieldGroup("Contraseña", txtPassword));
        card.add(Box.createVerticalStrut(10));
        card.add(lblError);
        card.add(Box.createVerticalStrut(18));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));
        

        root.add(card);
        setContentPane(root);

        // Enter key
        getRootPane().setDefaultButton(btnLogin);
        txtPassword.addActionListener(e -> intentarLogin());
    }

   
    private JLabel styledLabel(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JTextField buildField(String placeholder) {
        JTextField f = new JTextField();
        styleField(f, placeholder);
        return f;
    }

    private void styleField(JTextField f, String placeholder) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_WHITE);
        f.setBackground(FIELD_BG);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1, true),
                    new EmptyBorder(10, 14, 10, 14)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                    new EmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    private JPanel fieldGroup(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = styledLabel(label, 12, Font.PLAIN, TEXT_MUTED);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        field.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(field);
        return p;
    }

    private JButton buildLoginButton() {
        JButton btn = new JButton("Ingresar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_HOV : ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> intentarLogin());
        return btn;
    }

    // ── Lógica de login ───────────────────────────────────────
    private void intentarLogin() {
        lblError.setText(" ");
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Complete todos los campos.");
            return;
        }
        try {
            Usuario u = service.iniciarSesion(user, pass);
            abrirDashboard(u);
        } catch (CredencialesInvalidasException | UsuarioNoEncontradoException ex) {
            lblError.setText(ex.getMessage());
            txtPassword.setText("");
        }
    }

    private void abrirDashboard(Usuario usuario) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dash = new DashboardFrame(service);
            dash.setVisible(true);
        });
    }

    
    private int dragX, dragY;
    private void addDragSupport(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragX = e.getX(); dragY = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - dragX, getY() + e.getY() - dragY);
            }
        });
    }
}
