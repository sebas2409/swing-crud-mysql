package com.teoria;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Registro {
    private JPanel main;
    private JTextField tfProducto;
    private JTextField tfPrecio;
    private JTextField tfUnidades;
    private JButton btnSave;
    private JTable producTable;
    private JButton btnUpdate;
    private JComboBox comboBox1;
    private JButton btnDelete;
    private JButton btnFullTable;
    Connection connect;
    PreparedStatement ps;
    ResultSet rs;


    public static void main(String[] args) throws SQLException {
        JFrame frame = new JFrame("Registro");
        frame.setContentPane(new Registro().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
//conexión con clever cloud
    public void connection() throws SQLException {
        connect = DriverManager.getConnection("jdbc:mysql://uk2asvemw3ptqzx6:29cQvZ5EBuLqluglk4zq@bgxxqthcseyzlfu1x3fw-mysql.services.clever-cloud.com:3306/bgxxqthcseyzlfu1x3fw");
        System.out.println("Conectado con exito");
    }
//Carga de datos en la tabla
    public void tableLoad() throws SQLException {
        ps = connect.prepareStatement("select * from productos");
        rs = ps.executeQuery();
        producTable.setModel(DbUtils.resultSetToTableModel(rs));
    }
//Carga los id de los productos en el comboBox
    public void comboLoad() throws SQLException {
        ps = connect.prepareStatement("select id from productos");
        rs = ps.executeQuery();
        ArrayList<String> arr = new ArrayList<>();
        arr.clear();
        while(rs.next()) {
            arr.add(rs.getString(1));
        }
        comboBox1.setModel(new DefaultComboBoxModel(arr.toArray()));
    }

//Constructor y Listeners
    public Registro() throws SQLException {
        connection();
        tableLoad();
        comboLoad();
        btnFullTable.setEnabled(false);

        //Boton de guardar
        btnSave.addActionListener(e -> {
            try {
                ps = connect.prepareStatement("insert into productos(producto,precio,unidades) values (?,?,?)");
                ps.setString(1,tfProducto.getText());
                ps.setFloat(2,Float.parseFloat(tfPrecio.getText()));
                ps.setInt(3,Integer.parseInt(tfUnidades.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null,"Producto registrado con exito");
                tableLoad();
                comboLoad();
                tfProducto.setText("");
                tfPrecio.setText("");
                tfUnidades.setText("");
                tfProducto.requestFocus();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //ComboBox Listener
        comboBox1.addItemListener(e -> {
            try {
                ps = connect.prepareStatement("select * from productos where id=?");
                ps.setInt(1,Integer.parseInt(comboBox1.getSelectedItem().toString()));
                rs = ps.executeQuery();
                producTable.setModel(DbUtils.resultSetToTableModel(rs));
                btnFullTable.setEnabled(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        });

        //mostrar tabla completa
        btnFullTable.addActionListener(e -> {
            try {
                tableLoad();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //Eliminar fila de la tabla
        btnDelete.addActionListener(e -> {
            try {
                ps = connect.prepareStatement("delete from productos where producto=?");
                ps.setString(1,tfProducto.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null,"producto eliminado correctamente");
                tableLoad();
                comboLoad();
                tfProducto.setText("");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        });

    }
}
