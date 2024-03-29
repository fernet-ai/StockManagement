/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import dao.ProdottoDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
class CategoriePanel extends JPanel {

    private final DefaultTableModel model;
    public static Object[] nuovaRiga;
    public ArrayList<String> list_cat_new;
    private FramePrincipale frameprinc;
    public ArrayList<String> list_tot;
    private final JTable table;

    public CategoriePanel() {

        try {
            File file = new File("./DATA/CONFIG/aikkop.aksn");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list_cat_new = (ArrayList<String>) ois.readObject();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger("genlog").warning("ClassNotFoundException: \n" + StockManagement.printStackTrace(ex));
        } catch (FileNotFoundException ex) {
            File file = new File("./DATA/CONFIG/aikkop.aksn");
            Logger.getLogger("genlog").warning("FileNotFoundException:\n " + StockManagement.printStackTrace(ex));
        } catch (IOException ex) {
            Logger.getLogger("genlog").warning("IOException: \n" + StockManagement.printStackTrace(ex));
            list_cat_new = new ArrayList<>();
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("CATEGORIE");
        title.setFont(new Font("Arial Black", Font.BOLD, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        super.add(title);

        JPanel panSopra = new JPanel();
        panSopra.setLayout(new GridBagLayout());
        panSopra.setMaximumSize(new Dimension(1420, 300));
        JPanel cerca = new JPanel(new GridBagLayout());
        JLabel searchlabel = new JLabel("Cerca:");
        searchlabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        JTextField casella = new JTextField(20);
        casella.setBackground(Color.darkGray);
        cerca.add(searchlabel);
        cerca.add(casella);
        cerca.setBorder(new EmptyBorder(0, 0, 0, 800));
        panSopra.add(cerca);

        JButton buttonNew = new JButton("ADD NEW");
        buttonNew.setBackground(new Color( 165,204 ,107 ));
        buttonNew.setForeground(Color.black);
        //*************+* BOTTONE AGGIUNGI NUOVA RIGA**************************
        buttonNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddCategoriaDialog input = new AddCategoriaDialog();
                String txt = input.getName();
                input.setLocationRelativeTo(null);
                input.setVisible(true);

                try {
                    refreshTab();
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });
        buttonNew.setFont(new Font("Arial Black", Font.BOLD, 13));
        panSopra.add(buttonNew);
        JButton buttonModifica = new JButton("Modifica");
        buttonModifica.setBackground(new Color( 255,200 ,18 ));
        buttonModifica.setForeground(Color.black);
        buttonModifica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(getParent(), "Devi selezionare una riga da modificare!");
                    return;
                }
                //System.out.println("Categoria da modificare " + table.getValueAt(table.getSelectedRow(), 0));
                JFrame modificaframe = new JFrame("Specifica il nuovo valore per la categoria");
                modificaframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                modificaframe.setAlwaysOnTop(true);
                modificaframe.setLocationRelativeTo(null);
                modificaframe.setMinimumSize(new Dimension(500, 100));
                JTextField name = new JTextField(model.getValueAt(table.getSelectedRow(), 0).toString());
                JButton add = new JButton("Modifica");
                add.setForeground(Color.white);
                add.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        modificaframe.setAlwaysOnTop(false);
           
                        if (name.getText().length() < 2 || name.getText().length() > 22) {
                            modificaframe.setAlwaysOnTop(false);
                            JOptionPane.showMessageDialog(getParent(), "La lunghezza della categoria deve essere compresa tra 2 e 22 caratteri|");
                            modificaframe.setAlwaysOnTop(true);
                            return;
                        }
                        
                        
                        for(int i =0; i< model.getRowCount(); i++){
                            if(model.getValueAt(i, 0).toString().equals(name.getText().toUpperCase())){
                                JOptionPane.showMessageDialog(null, "Categoria già esistente!");
                                return;
                            }
                        }
                        
                           
                        // Se è una categoria dinamica:
                        if (model.getValueAt(table.getSelectedRow(), 1).toString().equals("DA DEFINIRE")) {
                            int index = list_cat_new.indexOf(model.getValueAt(table.getSelectedRow(), 0).toString());
                            list_cat_new.set(index, name.getText().toUpperCase());
                            model.setValueAt(name.getText().toUpperCase(), table.getSelectedRow(), 0);

                        } //Se è una categoria del db
                        else {
                            ProdottoDAO prodao = new ProdottoDAO();
                            try {
                                prodao.updateCat(model.getValueAt(table.getSelectedRow(), 0).toString(), name.getText().toUpperCase());
                                model.setValueAt(name.getText().toUpperCase(), table.getSelectedRow(), 0);

                            } catch (SQLException ex) {
                                Logger.getLogger("genlog").warning("SQLException:\n " + StockManagement.printStackTrace(ex));
                            }

                        }

                        modificaframe.dispose();
                        try {
                            refreshTab();
                        } catch (SQLException ex) {
                            Logger.getLogger("genlog").warning("SQLException:\n " + StockManagement.printStackTrace(ex));
                        }

                    }
                });

                modificaframe.setLayout(new GridLayout(2, 1, 5, 5));

                modificaframe.add(name);
                modificaframe.add(add);
                modificaframe.pack();
                modificaframe.setResizable(false);
                modificaframe.setVisible(true);
            }
        });

        buttonModifica.setFont(new Font("Arial Black", Font.BOLD, 13));
        panSopra.add(buttonModifica);
        JButton buttonCancella = new JButton("Cancella");
                buttonCancella.setBackground(new Color( 255,50 ,50 ));
        buttonCancella.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                

                // Se trattasi di categorie dinamiche
                if (model.getValueAt(table.getSelectedRow(), 1).toString().equals("DA DEFINIRE")) {
                    int OpzioneScelta = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler cancellare la categoria  " + model.getValueAt(table.getSelectedRow(), 0).toString() + "?");
                    if (OpzioneScelta == JOptionPane.OK_OPTION) {
                        int index = list_cat_new.indexOf(model.getValueAt(table.getSelectedRow(), 0).toString());
                        list_cat_new.remove(index);
                        model.removeRow(table.getSelectedRow());
                    }
                } // Trattasi di categoria del db
                else {
                    Object[] options = {"Cancella solo categoria", "Cancella TUTTI i prodotti correlati alla categoria"};
                    int scelta = JOptionPane.showOptionDialog(null, "Seleziona la modalità di eliminazione", "Elimina categoria "+model.getValueAt(table.getSelectedRow(), 0).toString(), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                    // Cancella solo categoria
                    if (scelta == 0) {
                        ProdottoDAO prodao = new ProdottoDAO();
                        try {
                            if (model.getValueAt(table.getSelectedRow(), 0).toString().equals("NULL")) {
                                JOptionPane.showMessageDialog(null, "Ma che fai? Mica puoi cancellare il niente!");
                                actionPerformed(e);
                                return;
                            }
                            prodao.updateCat(model.getValueAt(table.getSelectedRow(), 0).toString(), "NULL");
                            model.setValueAt("", table.getSelectedRow(), 0);
                        } catch (SQLException ex) {
                            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                        }

                    } // Cancella prodotti correlati
                    else if (scelta == 1) {
                        ProdottoDAO prodao = new ProdottoDAO();
                        try {
                            prodao.removeCatp(model.getValueAt(table.getSelectedRow(), 0).toString());
                        } catch (SQLException ex) {
                            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                        }

                    }
                }
                try {
                    refreshTab();
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });
        buttonCancella.setFont(new Font("Arial Black", Font.BOLD, 13));
        
        panSopra.add(buttonCancella);

        super.add(panSopra);

        //Tabella
        JPanel TitoloTab1 = new JPanel();
        TitoloTab1.setLayout(new GridLayout(1, 1));
        TitoloTab1.setBorder(new EmptyBorder(0, 100, 20, 100));

        String[] columnNames = {"Nome", "Quantità", "Query"};
        Object[][] data = {};

        model = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return column >= 2; //il numero di celle editabili...
            }
        };
        table = new JTable(model){
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
            refreshTab();
        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
        }

        table.setRowHeight(40); //altezza celle

        //X colonne che hanno pulsanti
        table.getColumnModel().getColumn(2).setCellRenderer(new ClientsTableButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ClientsTableRenderer(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        TitoloTab1.add(sp);

        //******* funzione di ricerca *******************+
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(rowSorter);
        
        rowSorter.setSortable(2, false);// toglie il sorting alla colonna query
        rowSorter.setSortable(1, false);
        rowSorter.setSortable(0, false);
        

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

    public void setComunicator(FramePrincipale princ) {
        frameprinc = princ;

    }

    public ImageIcon ImpostaImg(String nomeImmag) {

        ImageIcon icon = new ImageIcon(nomeImmag);
        Image ImmagineScalata = icon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        icon.setImage(ImmagineScalata);
        return icon;
    }

    public void refreshTab() throws SQLException {

        //Cancello vecchie righe...
        model.setRowCount(0);

        ProdottoDAO dao = new ProdottoDAO();

        Enumeration names;
        String key;
        names = dao.getCatAndSum().keys();

        
        // Aggiorno con le nuove
        for (String catDinamica : list_cat_new) {

            model.addRow(new Object[]{catDinamica, "DA DEFINIRE", "Vai a prodotti"});

        }

        
        while (names.hasMoreElements()) {
            key = (String) names.nextElement(); //Nome categoria del db
            if (list_cat_new.contains(key)) {
                list_cat_new.remove(key); // ELIMINO DUPLICATI IN LIST CAT_NEW
            }
            model.addRow(new Object[]{key, dao.getCatAndSum().get(key), "Vai a prodotti"});
        }


        // Agiorno il file con le nuove cat_dinamiche
        try {
            File output = new File("./DATA/CONFIG/aikkop.aksn");
            FileOutputStream fos;
            fos = new FileOutputStream(output);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list_cat_new);

        } catch (FileNotFoundException ex) {
            Logger.getLogger("genlog").warning("FileNotFoundException:\n" + StockManagement.printStackTrace(ex));
        } catch (IOException ex) {
            Logger.getLogger("genlog").warning("IOException:\n" + StockManagement.printStackTrace(ex));
        }

    }

    class ClientsTableButtonRenderer extends JButton implements TableCellRenderer {

        public ClientsTableButtonRenderer() {

            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(Color.white);

            setText("Vai a prodotti");
            setIcon(ImpostaImg("prodotti.png"));

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
            setForeground(Color.white);

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
            button.setIcon(ImpostaImg("prodotti.png"));
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) // SE CLICCATO QUEL BOTTONE:::::::::::::
            {
                if (button.getText().equals("Vai a prodotti")) {
                    frameprinc.VaiAProdotti(table.getValueAt(row, 0).toString());
                    //PRENDO RIFERIMENTO DI HOME PANEL E CHIAMO IL SUO METODO PER CAMBIARE VERSO LA SCHERMATA PRODOTTI

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

    public class AddCategoriaDialog extends JDialog {

        private JTextField name;

        public AddCategoriaDialog() {
            setTitle("Aggiungi una categoria");
            setAlwaysOnTop(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            this.setMinimumSize(new Dimension(500, 100));
            this.name = new JTextField();
            name.addKeyListener(new KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        try {
                            confermaCategoria();
                        } catch (SQLException ex) {
                            Logger.getLogger("genlog").warning("SQLExceprion:\n" + StockManagement.printStackTrace(ex));
                        }
                    }
                }
            });

            JButton add = new JButton("Conferma nuova categoria");
            add.setForeground(Color.white);
            add.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                
                    try {
                        confermaCategoria();
                    } catch (SQLException ex) {
                        Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));                    }
                    
                }
            });

            this.setLayout(new GridLayout(2, 1, 5, 5));

            this.add(name);
            this.add(add);
            this.pack();
            this.setResizable(false);
        }

        private void close() {
            this.dispose();
        }

        public String getName() {
            return this.name.getText().toUpperCase();
        }

        public void confermaCategoria() throws SQLException {
            
            setAlwaysOnTop(false);

            for(int i =0; i< model.getRowCount(); i++){
                if(model.getValueAt(i, 0).toString().equals(name.getText().toUpperCase())){
                    JOptionPane.showMessageDialog(null, "Categoria già esistente!");
                    return;
                }
            }
            
            if (name.getText().length() < 2 || name.getText().length() > 22) {
                JOptionPane.showMessageDialog(getParent(), "La categoria deve avere lunghezza compresa tra 2 e 22 caratteri!");
            } else {
                Logger.getLogger("userlog").info("Hai aggiunto la categoria: " + name.getText());
                model.addRow(new Object[]{name.getText().toUpperCase(), "DA DEFINIRE", "Vai a prodotti"});
                list_cat_new.add(name.getText().toUpperCase());
                frameprinc.prodotti.list_cat_new.add(name.getText().toUpperCase());
                close();
                refreshTab();

            }
            try {

                File output = new File("./DATA/CONFIG/aikkop.aksn");
                FileOutputStream fos;
                fos = new FileOutputStream(output);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list_cat_new);

            } catch (FileNotFoundException ex) {
                Logger.getLogger("genlog").warning("FileNotFoundException:\n" + StockManagement.printStackTrace(ex));
            } catch (IOException ex) {
                Logger.getLogger("genlog").warning("IOException:\n" + StockManagement.printStackTrace(ex));
            }
        }
    }

}
