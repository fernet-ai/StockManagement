/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import beans.Prodotto;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dao.ProdottoDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Fernet
 */
public class CodiciPanel extends JPanel {

    private final DefaultTableModel model;
    public static Object[] nuovaRiga;

    public CodiciPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("CODICI");
        title.setFont(new Font("Arial Black", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        super.add(title);

        JPanel panSopra = new JPanel();
        panSopra.setMaximumSize(new Dimension(1420, 300));
        JPanel cerca = new JPanel();
        JLabel searchlabel = new JLabel("Cerca:");
        searchlabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        JTextField casella = new JTextField(20);
        casella.setBackground(Color.darkGray);
        cerca.add(searchlabel);
        cerca.add(casella);
        cerca.setBorder(new EmptyBorder(0, 0, 0, 900));
        panSopra.add(cerca);

        panSopra.add(new JLabel(" "));

        JButton buttonNew = new JButton("Apri cartella");
         buttonNew.setBackground(new Color( 255,200 ,18 ));
        buttonNew.setForeground(Color.black);
        //*************+* STAMPA SELEZIONATI**************************
        buttonNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {


                //refresh
                try {
                    refreshTab();
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }

                try {
                    Desktop.getDesktop().open(new File("./DATA/QRCODE/"));
                } catch (IOException ex) {
                    Logger.getLogger("genlog").warning("IOException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });
        buttonNew.setFont(new Font("Arial Black", Font.BOLD, 15));
        panSopra.add(buttonNew);

        super.add(panSopra);

        //Tabella
        JPanel TitoloTab1 = new JPanel();
        TitoloTab1.setLayout(new GridLayout(1, 1));
        TitoloTab1.setBorder(new EmptyBorder(0, 100, 20, 100));

        String[] columnNames = {"SKU", "QR", "Stampa QR"};
        Object[][] data = {};

        model = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return column >= 2; //il numero di celle editabili...
            }
        };
        JTable table = new JTable(model){
         public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component comp = super.prepareRenderer(renderer, row, column);
            Color alternateColor = new Color(24, 53, 90);
            Color whiteColor = new Color(10, 25, 43);
            if(!comp.getBackground().equals(getSelectionBackground())) {
               Color c = (row % 2 == 0 ? alternateColor : whiteColor);
               comp.setBackground(c);
               c = null;
            }
            return comp;
         }
      };
        table.getTableHeader().setReorderingAllowed(false);

        try {
            refreshTab(); // Aggiorna tavola con  i fornitori del db;
        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
        }

        table.setRowHeight(150); //altezza celle

        //X colonne che hanno pulsanti
        table.getColumnModel().getColumn(1).setCellRenderer(new ClientsTableButtonRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new ClientsTableRenderer(new JCheckBox()));
        table.getColumnModel().getColumn(2).setCellRenderer(new ClientsTableButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ClientsTableRenderer(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        TitoloTab1.add(sp);

        //******* funzione di ricerca *******************+
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        rowSorter.setSortable(1, false);
        rowSorter.setSortable(2, false);

        casella.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                String text = casella.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter(text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                String text = casella.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter(text));
                }
            }
        });

        super.add(TitoloTab1);

    }

    public ImageIcon ImpostaImg(String nomeImmag) {

        ImageIcon icon = new ImageIcon((getClass().getResource(nomeImmag)));
        Image ImmagineScalata = icon.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT);
        icon.setImage(ImmagineScalata);
        return icon;
    }

    public void refreshTab() throws SQLException {

        model.setRowCount(0);

        ProdottoDAO dao = new ProdottoDAO();

        // Aggiorno con le nuove
        for (Prodotto prod : dao.getAll()) {

            model.addRow(new Object[]{prod.getSku(), "", "Stampa"});

        }

    }

    class ClientsTableButtonRenderer extends JButton implements TableCellRenderer {

        public ClientsTableButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Stampa");
            //setIcon(ImpostaImg("printer.png"));

            setText((value == null) ? "" : value.toString());
            if (getText().equals("Stampa")) {
                setIcon(ImpostaImg("/res/img/printer.png"));
            } else { //Altrimenti imposta il suo qr CODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! settare come icona il qrCode relativo allo sku di questa riga
                String percorsoQR1 = "./DATA/QRCODE/"+table.getValueAt(row, 0).toString();
                String percorsoQR = percorsoQR1.substring(0, percorsoQR1.indexOf('-'))+ ".png";
                setIcon(new ImageIcon(percorsoQR));
                setText(percorsoQR);

            }
            return this;
        }
    }

    public class ClientsTableRenderer extends DefaultCellEditor {

        private JButton button;
        private String label;
        private boolean clicked;
        private int row, col;
        private JTable table;

        public ClientsTableRenderer(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();    // ECCO IL BOTTONE
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            this.col = column;

            button.setForeground(Color.black);
            button.setBackground(UIManager.getColor("Button.background"));
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            if (button.getText().equals("Stampa")) {
                button.setIcon(ImpostaImg("/res/img/printer.png"));
            } else {

            }
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) // SE CLICCATO QUEL BOTTONE:::::::::::::
            {
                //Se Ho premuto Cancella ....
                if (button.getText().equals("Cancella")) {
                    int OpzioneScelta = JOptionPane.showConfirmDialog(getComponent(), "Sicuro di voler cancellare la riga:\n [  "
                            + table.getValueAt(row, 0) + "  |   "
                            + table.getValueAt(row, 1) + "  ]");
                    if (OpzioneScelta == JOptionPane.OK_OPTION) {
                        // QUI METTERE IL DAO CON FUNZIONE REMOVE
                    }

                } else if (button.getText().equals("Stampa")) {
                    String path = table.getValueAt(row, 0).toString();
                    try {
                        Desktop.getDesktop().open(new File("./DATA/QRCODE/" + path.substring(0, path.indexOf("-")) + ".png"));
                    } catch (IOException ex) {
                        Logger.getLogger("genlog").warning("IOException\n" + StockManagement.printStackTrace(ex));
                    }

                }

            }
            clicked = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

    }

    public String generaQRdaSKU(String sku) throws IOException, WriterException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(sku, BarcodeFormat.QR_CODE, 130, 130);

        Path path = FileSystems.getDefault().getPath("./DATA/QRCODE/" + sku.substring(0, sku.indexOf("-")) + ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return "./DATA/QRCODE/" + sku.substring(0, sku.indexOf("-")) + ".png"; //Ritorna il percorso del QR generato
    }
}
