import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class PEP3T_4_JCR extends JFrame {
    private JTextField campoCodigoMatricula;
    private JTextField campoNombreAsignatura;
    private JTextField campoNota1;
    private JTextField campoNota2;
    private JLabel mensaje;

    // Conexion base de datos
    private static final String url = "jdbc:mariadb://localhost:3306/PUNTU4";
    private static final String usuario = "root";
    private static final String contraseña = "root";

    public PEP3T_4_JCR() {

        JFrame marco = new JFrame("Ejercicio PEP3T_4 JAVA");

        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        // Crear un panel Norte
        JPanel panelNorte = new JPanel();
        panelNorte.setBorder(padding);
        JLabel titulo = new JLabel("GESTIÓN DE LA TABLA NOTAS");
        panelNorte.add(titulo);

        // Crear un panel Centro
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(padding);

        // Crear panel norte del panel Centro
        JPanel panelCentroNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel codigo_matricula = new JLabel("Código Matrícula: ");
        campoCodigoMatricula = new JTextField(8);
        panelCentroNorte.add(codigo_matricula);
        panelCentroNorte.add(campoCodigoMatricula);

        // Crear panel centro del panel Centro
        JPanel panelCentroCentro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nombre_asignatura = new JLabel("Nombre Asignatura: ");
        campoNombreAsignatura = new JTextField(20);
        panelCentroCentro.add(nombre_asignatura);
        panelCentroCentro.add(campoNombreAsignatura);

        // Crear panel sur del panel Centro
        JPanel panelCentroSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nota1 = new JLabel("Nota 1: ");
        campoNota1 = new JTextField(5);
        JLabel nota2 = new JLabel("Nota 2: ");
        campoNota2 = new JTextField(5);
        panelCentroSur.add(nota1);
        panelCentroSur.add(campoNota1);
        panelCentroSur.add(nota2);
        panelCentroSur.add(campoNota2);

        panelCentro.add(panelCentroNorte, BorderLayout.NORTH);
        panelCentro.add(panelCentroCentro, BorderLayout.CENTER);
        panelCentro.add(panelCentroSur, BorderLayout.SOUTH);

        // Crear panel Sur
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(padding);
        JPanel panelSurNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonInsertar = new JButton("Insertar");
        JButton botonModificar = new JButton("Modificar");
        JButton botonBorrar = new JButton("Borrar");
        JButton botonConsultar = new JButton("Consultar");
        panelSurNorte.add(botonInsertar);
        panelSurNorte.add(botonModificar);
        panelSurNorte.add(botonBorrar);
        panelSurNorte.add(botonConsultar);

        JPanel panelSurCentro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        mensaje = new JLabel(" ");
        panelSurCentro.add(mensaje);

        panelSur.add(panelSurNorte, BorderLayout.NORTH);
        panelSur.add(panelSurCentro, BorderLayout.CENTER);

        campoCodigoMatricula.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mensaje.setText(" ");
                campoNombreAsignatura.setText("");
                campoNota1.setText("");
                campoNota2.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mensaje.setText(" ");
                campoNombreAsignatura.setText("");
                campoNota1.setText("");
                campoNota2.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mensaje.setText(" ");
                campoNombreAsignatura.setText("");
                campoNota1.setText("");
                campoNota2.setText("");
            }
        });

        campoCodigoMatricula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    consultarDatos();
                }
            }
        });

        botonInsertar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarDatos();
            }
        });

        botonModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarDatos();
            }
        });

        botonBorrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarDatos();
            }
        });

        botonConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarDatos();
            }
        });

        marco.add(panelNorte, BorderLayout.NORTH);
        marco.add(panelCentro, BorderLayout.CENTER);
        marco.add(panelSur, BorderLayout.SOUTH);

        marco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        marco.setVisible(true);
        marco.pack();
        marco.setLocationRelativeTo(null);
    }

    private void insertarDatos() {
        String codigo = campoCodigoMatricula.getText();
        String asignatura = campoNombreAsignatura.getText();
        String nota1 = campoNota1.getText().replace(",", ".");
        String nota2 = campoNota2.getText().replace(",", ".");

        if (codigo.isEmpty() || asignatura.isEmpty() || nota1.isEmpty() || nota2.isEmpty()) {
            mensaje.setText("Faltan datos por rellenar");
            return;
        }

        try {
            double nota1Num = Double.parseDouble(nota1);
            double nota2Num = Double.parseDouble(nota2);

            if (nota1Num < 0 || nota1Num > 10 || nota2Num < 0 || nota2Num > 10) {
                mensaje.setText("Las notas introducidas deben estar entre 0 y 10");
                return;
            }

            // Insertar datos en la base de datos
            Connection conexBd = DriverManager.getConnection(url, usuario, contraseña);
            String insertar = "INSERT INTO NOTAS (codigo_matricula, nombre_asignatura, nota1, nota2) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conexBd.prepareStatement(insertar);
            ps.setString(1, codigo);
            ps.setString(2, asignatura);
            ps.setDouble(3, nota1Num);
            ps.setDouble(4, nota2Num);

            int filActualizadas = ps.executeUpdate();
            if (filActualizadas > 0) {
                mensaje.setText("Registro insertado");
            } else {
                mensaje.setText("Error al insertar los datos");
            }

            ps.close();
            conexBd.close();

        } catch (NumberFormatException e) {
            mensaje.setText("Las notas deben ser números");
        } catch (SQLIntegrityConstraintViolationException e) {
            mensaje.setText("El código de matrícula ya existe");
        } catch (SQLException e) {
            mensaje.setText("Error de la base de datos " + e.getMessage());
        }
    }

    private void modificarDatos() {
        String codigo = campoCodigoMatricula.getText();
        String asignatura = campoNombreAsignatura.getText();
        String nota1 = campoNota1.getText().replace(",", ".");
        String nota2 = campoNota2.getText().replace(",", ".");

        if (codigo.isEmpty()) {
            mensaje.setText("El código de matrícula es obligatorio");
            return;
        }

        try {
            double nota1Num = Double.parseDouble(nota1);
            double nota2Num = Double.parseDouble(nota2);

            if (nota1Num < 0 || nota1Num > 10 || nota2Num < 0 || nota2Num > 10) {
                mensaje.setText("Las notas introducidas deben estar entre 0 y 10");
                return;
            }

            // Insertar datos en la base de datos
            Connection conexBd = DriverManager.getConnection(url, usuario, contraseña);
            String insertar = "UPDATE NOTAS SET nombre_asignatura = ?, nota1 = ?, nota2 = ? WHERE codigo_matricula = ?";
            PreparedStatement ps = conexBd.prepareStatement(insertar);
            ps.setString(1, asignatura);
            ps.setDouble(2, nota1Num);
            ps.setDouble(3, nota2Num);
            ps.setString(4, codigo);

            int filActualizadas = ps.executeUpdate();
            if (filActualizadas > 0) {
                mensaje.setText("Registro modificado");
            } else {
                mensaje.setText("Error al modificar los datos");
            }

            ps.close();
            conexBd.close();

        } catch (NumberFormatException e) {
            mensaje.setText("Las notas deben ser números");
        } catch (SQLIntegrityConstraintViolationException e) {
            mensaje.setText("El código de matrícula ya existe");
        } catch (SQLException e) {
            mensaje.setText("Error de la base de datos " + e.getMessage());
        }
    }

    private void eliminarDatos() {
        String codigo = campoCodigoMatricula.getText();

        if (codigo.isEmpty()) {
            mensaje.setText("El código de matrícula es obligatorio");
            return;
        }

        try {
            // Insertar datos en la base de datos
            Connection conexBd = DriverManager.getConnection(url, usuario, contraseña);
            String insertar = "DELETE FROM NOTAS WHERE codigo_matricula = ?";
            PreparedStatement ps = conexBd.prepareStatement(insertar);
            ps.setString(1, codigo);

            int filActualizadas = ps.executeUpdate();
            if (filActualizadas > 0) {
                mensaje.setText("Registro eliminado");
            } else {
                mensaje.setText("Error al eliminar los datos");
            }

            ps.close();
            conexBd.close();

        } catch (SQLException e) {
            mensaje.setText("Error de la base de datos " + e.getMessage());
        }
    }


    private void consultarDatos() {
        String codigo = campoCodigoMatricula.getText();

        if (codigo.isEmpty()) {
            mensaje.setText("El código de matrícula es obligatorio");
            return;
        }

        try {
            // Insertar datos en la base de datos
            Connection conexBd = DriverManager.getConnection(url, usuario, contraseña);
            String insertar = "SELECT * FROM NOTAS WHERE codigo_matricula = ?";
            PreparedStatement ps = conexBd.prepareStatement(insertar);
            ps.setString(1, codigo);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                campoNombreAsignatura.setText(rs.getString("nombre_asignatura"));
                campoNota1.setText(String.valueOf(rs.getDouble("nota1")));
                campoNota2.setText(String.valueOf(rs.getDouble("nota2")));
                mensaje.setText("Registro encontrado");
            } else {
                mensaje.setText("Registro no encontrado");
            }

            ps.close();
            conexBd.close();

        } catch (SQLException e) {
            mensaje.setText("Error de la base de datos " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new PEP3T_4_JCR();
    }
}
