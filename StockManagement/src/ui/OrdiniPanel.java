/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import beans.Prodotto;
import dao.OrdineDAO;
import dao.ProdottoDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import others.RoundedPanel;

/**
 *
 * @author Fernet
 */
public class OrdiniPanel extends JPanel {

    public Prodotto prodottoCorrente;
    private final ProdottoDAO dao;
    private final JLabel infosku;
    private final JLabel infonome;
    private final JLabel infocat;
    private final JTextArea infonote;
    private final JLabel infostock;
    private final JTextField casella;
    private final JLabel qty;
    private int qtyAttuale;
    private int qtyMin;
    public FramePrincipale frameprinc;

    public OrdiniPanel() {

        dao = new ProdottoDAO();
        qtyAttuale = 0;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel top = new JPanel();
        
        JLabel title = new JLabel("PRELEVA");
        title.setFont(new Font("Arial Black", Font.BOLD, 30));
        top.add(title);
          
        JButton switchOrd = new JButton(ImpostaImgSwitch("/res/img/refresh.png"));
        switchOrd.setBackground(UIManager.getColor("nimbusBase"));
        switchOrd.setText(" Passa a 'Ordini'");
        switchOrd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameprinc.OrdiniStatus = false;
                frameprinc.VaiAOrdini();
            }
        });
                
        top.add(switchOrd);
                
                   
        super.add(top);
        

        JPanel cerca = new JPanel();
        JLabel searchlabel = new JLabel("Cerca prodotto:");
        searchlabel.setFont(new Font("Arial Black", Font.BOLD, 20));
        casella = new JTextField(30);
        casella.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                try {

                    aggiornaScheda(casella.getText());
                } catch (SQLException ex) {
                    casella.setBackground(Color.red);
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });

        cerca.add(searchlabel);
        cerca.add(casella);
        cerca.setMaximumSize(new Dimension(1000, 100));

        super.add(cerca);

        //Pannello centrale
        JPanel princ = new JPanel();
        princ.setLayout(new GridLayout(2, 2, 40, 40));

        JPanel fotopan = new JPanel();
        princ.add(fotopan); //Aggiungi pannello x Foto a Ovest
        fotopan.setBackground(new Color(128, 128, 128));
        fotopan.setBorder(BorderFactory.createLineBorder(new Color(27, 32, 36), 50));
        fotopan.add(new JLabel("FOTO"));

        JPanel info = new JPanel();
        info.setLayout(new GridLayout(5, 2, 20, 20));
        info.setBorder(BorderFactory.createLineBorder(new Color(27, 32, 36), 20));

        JLabel infolabel = new JLabel("SKU prodotto:");
        infolabel.setFont(new Font("Arial Black", Font.BOLD, 30));
        infolabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infolabel);

        infosku = new JLabel("");
        infosku.setFont(new Font("Arial Black", Font.BOLD, 20));
        infosku.setForeground(new Color(244, 80, 37));
        infosku.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infosku);

        JLabel infolabel2 = new JLabel("Nome:");
        infolabel2.setFont(new Font("Arial Black", Font.BOLD, 20));
        infolabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infolabel2);

        infonome = new JLabel("");
        infonome.setFont(new Font("Arial Black", Font.BOLD, 20));
        infonome.setForeground(new Color(244, 80, 37));
        infonome.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infonome);

        JLabel infolabel3 = new JLabel("Categoria:");
        infolabel3.setFont(new Font("Arial Black", Font.BOLD, 20));
        infolabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infolabel3);

        infocat = new JLabel("");
        infocat.setFont(new Font("Arial Black", Font.BOLD, 20));
        infocat.setForeground(new Color(244, 80, 37));
        infocat.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infocat);

        JLabel infolabel4 = new JLabel("Note:");
        infolabel4.setFont(new Font("Arial Black", Font.BOLD, 20));
        infolabel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infolabel4);

        infonote = new JTextArea("");
        infonote.setFont(new Font("Arial Black", Font.ITALIC, 15));
        infonote.setEditable(false);
        infonote.setLineWrap(true);
        infonote.setForeground(Color.white);
        infonote.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infonote);

        JLabel infolabel5 = new JLabel("In Stock?:");
        infolabel5.setFont(new Font("Arial Black", Font.BOLD, 20));
        infolabel5.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infolabel5);

        infostock = new JLabel("");
        infostock.setFont(new Font("Arial Black", Font.BOLD, 20));
        infostock.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(infostock);

        princ.add(info);

        /**
         * * PRENDI E CONFERMA *********
         */
        JPanel SXdown = new RoundedPanel();
        SXdown.setLayout(new GridLayout(6, 1));
        SXdown.add(new JLabel(""));
        SXdown.add(new JLabel(""));

        JPanel quantityPanel = new JPanel(new GridBagLayout());
        JLabel infolabel6 = new JLabel("Quantità:   ");
        infolabel6.setFont(new Font("Arial Black", Font.BOLD, 30));
        quantityPanel.add(infolabel6);
        qty = new JLabel("");
        qty.setFont(new Font("Arial Black", Font.BOLD, 40));
        qty.setForeground(new Color(244, 80, 37));
        quantityPanel.add(qty);
        SXdown.add(quantityPanel);

        JPanel quantityPanel2 = new JPanel(new GridBagLayout());
        JLabel daprendere = new JLabel("Prendi:  ");
        daprendere.setFont(new Font("Arial Black", Font.BOLD, 30));
        quantityPanel2.add(daprendere);
        JTextField quantdaprend = new JTextField(10);
        quantdaprend.setFont(new Font("Arial Black", Font.BOLD, 30));
        quantityPanel2.add(quantdaprend);
        SXdown.add(quantityPanel2);

        SXdown.add(new JLabel(""));
        SXdown.add(new JLabel(""));

        princ.add(SXdown);

        JPanel DXdown = new JPanel(new GridBagLayout());
        JButton conferma = new JButton("   Conferma    ");
        conferma.setFont(new Font("Arial Black", Font.ITALIC, 40));
        conferma.setMinimumSize(new Dimension(100, 50));
        conferma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (check()) {
                    int OpzioneScelta = JOptionPane.showConfirmDialog(getParent(), "Sei sicuro di voler effettuare tali modifiche?");
                    if (OpzioneScelta == JOptionPane.OK_OPTION) {
                        System.out.println("OOOOOOOOKKKKKK EFFETTUO PRELIEVO UNITA'");

                        prodottoCorrente.setQty(qtyAttuale - Integer.parseInt(quantdaprend.getText()));
                        OrdineDAO ordao = new OrdineDAO();
                        try {
                            dao.update(prodottoCorrente);
                             Logger.getLogger("userlog").info("Ho prelevato:\n"+"Sku= " + prodottoCorrente.getSku() + " Qty prelevata= " +quantdaprend.getText());
                            casella.setText(prodottoCorrente.getSku());
                        } catch (SQLException ex) {
                            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                        }
                    }
                }
            }

            private boolean check() {

                int qtydaTogliere;
                try { //Controlla se sono interi...
                    qtydaTogliere = Integer.parseInt(quantdaprend.getText());

                } catch (NumberFormatException e) {
                    Logger.getLogger("genlog").warning("NumberFormatException\n" + StockManagement.printStackTrace(e));
                    return false;
                }

                qtydaTogliere = Integer.parseInt(quantdaprend.getText());

                if (!prodottoCorrente.isInstock()) {
                    JOptionPane.showMessageDialog(getParent(), "Il prodotto non è in stock !!!");
                    return false;
                }
                if (qtydaTogliere <= 0) {
                    JOptionPane.showMessageDialog(getParent(), "inserisci una quantità positiva");
                    return false;
                }

                if (qtydaTogliere > (qtyAttuale - qtyMin)) {
                    JOptionPane.showMessageDialog(getParent(), "Non puoi prendere più di " + (qtyAttuale - qtyMin) + " unità!");
                    return false;
                }

                if (qtydaTogliere > qtyAttuale) {
                    JOptionPane.showMessageDialog(getParent(), "Non puoi prendere più di " + qtyAttuale + " unità!");
                    return false;
                }

                return true;

            }
        });
        DXdown.add(conferma);
        princ.add(DXdown);

        super.add(princ);

    }

    public void aggiornaScheda(String sku) throws SQLException {

        prodottoCorrente = dao.getBySku(sku);

        casella.setBackground(Color.green);

        infosku.setText(prodottoCorrente.getSku());
        infonome.setText(prodottoCorrente.getNome());
        infocat.setText(prodottoCorrente.getCategoria());
        infonote.setText(prodottoCorrente.getNote());
        qtyAttuale = prodottoCorrente.getQty();

        qtyMin = prodottoCorrente.getQty_min();
        qty.setText(Integer.toString(prodottoCorrente.getQty()) + " Min = " + prodottoCorrente.getQty_min());
        if (prodottoCorrente.getQty() <= prodottoCorrente.getQty_min()) {
            qty.setForeground(Color.red);
        } else {
            qty.setForeground(Color.green);
        }

        if (prodottoCorrente.isInstock()) {
            infostock.setText("SI");
            infostock.setForeground(Color.green);

        } else {
            infostock.setText("NO");
            infostock.setForeground(Color.red);

        }

    }
    
        public ImageIcon ImpostaImgSwitch(String nomeImmag) {

        ImageIcon icon = new ImageIcon((getClass().getResource(nomeImmag)));
        Image ImmagineScalata = icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        icon.setImage(ImmagineScalata);
        return icon;
    }  
        
        
    public void setComunicator(FramePrincipale princ) {
        frameprinc = princ;

    }
}
