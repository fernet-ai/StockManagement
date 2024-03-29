/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import beans.Ordine;
import others.JavaProcessId;
import beans.Prodotto;
import beans.Utente;
import dao.OrdineDAO;
import dao.ProdottoDAO;
import dao.UtenteDAO;
import database.DriverManagerConnectionPool;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import others.RoundedPanel;

/**
 *
 * @author Fernet
 */
public class FramePrincipale extends JFrame {

    private JPanel HomePanel;
    private CardLayout cardlayout;
    private JPanel pannellolaterale;
    private JPanel pannelloOpzioni;
    public ProdottiPanel prodotti;
    public CategoriePanel categorie;
    private CodiciPanel codici;
    private OrdiniPanel ordini;
    private OrdiniAdminPanel ordiniadmin;
    private DefaultTableModel model;
    private JTable table;
    private DefaultTableModel model2;
    private JTable table2;
    public String nomeuser;
    public Utente user;
    public ButtonDash button;
    public ButtonDash button1;
    public ButtonDash button2;
    public ButtonDash button3;
    public ButtonDash button4;
    public ButtonDash button5;
    private AnagrafichePanel anagrafiche;
    public boolean OrdiniStatus = false;
    private JLabel TitleLaterale;
    public ButtonLaterale lateralDashProdotti;
    private ButtonLaterale lateralDashAnagrafiche;
    private ButtonLaterale lateralDashCategoria;
    private ButtonLaterale lateralDashCodici;
    private ButtonLaterale lateralDashOrdini;
    private ButtonLaterale lateralDashReport;
    private ButtonLaterale lateralDashDash;

    public FramePrincipale(String nomeutente) {
        nomeuser = nomeutente;
        CreaGUI();
        refresh();
    }

    public void CreaGUI() {

        UtenteDAO daouten = new UtenteDAO();
        try {
            user = daouten.getByID(nomeuser);
        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
        }

        //set icona finestra
        ImageIcon img = new ImageIcon((getClass().getResource("/res/img/logo.png")));
        this.setIconImage(img.getImage());

        //Aggiungi barra Menu
        CreaMenu();

        //***** AGGIUNGI LE CARTE/SCHERMATE *********
        HomePanel = new JPanel(new CardLayout());
        cardlayout = new CardLayout();
        HomePanel.setLayout(cardlayout);

        //Aggiungi la carta "DASHBOARD"
        JPanel dashboard = new JPanel();
        HomePanel.add(dashboard, "Dashboard");

        ordini = new OrdiniPanel();
        HomePanel.add(ordini, "Preleva");
        ordini.setComunicator(this);

        //Aggiungi la carta "ANAGRAFICHE"
        anagrafiche = new AnagrafichePanel();
        HomePanel.add(anagrafiche, "Anagrafiche");
        anagrafiche.setComunicator(this);

        // Aggiungi la carta "CATEGORIE"
        categorie = new CategoriePanel();
        HomePanel.add(categorie, "Categorie");
        categorie.setComunicator(this);

        // Aggiungi la carta "PRODOTTO"
        prodotti = new ProdottiPanel();
        HomePanel.add(prodotti, "Prodotti");
        prodotti.setComunicator(this);

        // Aggiungi la carta "CODICI"
        codici = new CodiciPanel();
        HomePanel.add(codici, "Codici");

        //Aggiungi la carta "ORDINIADMIN"
        ordiniadmin = new OrdiniAdminPanel(nomeuser);
        HomePanel.add(ordiniadmin, "Ordini"); //Se è admin allora va sempre con etichetta "ordini"
        ordiniadmin.setComunicator(this);

        //Aggiungi la carta "REPORT"
        JPanel report = new JPanel();
        report.add(new JLabel(" R E P O R T !  ! !"));
        HomePanel.add(report, "Report");

        //Barra Laterale (rimarrà fissa per ogni schermata)
        pannellolaterale = new JPanel();
        TitleLaterale = new JLabel("User: " + nomeuser); //Per dare ampiezza al jpanel
        TitleLaterale.setFont(new Font("monospace", Font.BOLD, 18));
        pannellolaterale.setLayout(new BoxLayout(pannellolaterale, BoxLayout.Y_AXIS));
        TitleLaterale.setAlignmentX(CENTER_ALIGNMENT);

        pannellolaterale.add(TitleLaterale);

        pannelloOpzioni = new JPanel();
        pannelloOpzioni.setBackground(new Color(38, 44, 70));
        pannelloOpzioni.setLayout(new BoxLayout(pannelloOpzioni, BoxLayout.Y_AXIS));
        
        
        lateralDashDash = new ButtonLaterale("Dashboard");
        lateralDashAnagrafiche = new ButtonLaterale("Anagrafiche");
        lateralDashProdotti = new ButtonLaterale("Prodotti");
        lateralDashCategoria = new ButtonLaterale("Categorie");
        lateralDashCodici = new ButtonLaterale("Codici");
        lateralDashOrdini = new ButtonLaterale("Ordini");
        lateralDashReport = new ButtonLaterale("Report");
        
        pannelloOpzioni.add(lateralDashDash);
        pannelloOpzioni.add(lateralDashAnagrafiche);
        pannelloOpzioni.add(lateralDashCategoria);
        pannelloOpzioni.add(lateralDashProdotti);
        pannelloOpzioni.add(lateralDashCodici);
        pannelloOpzioni.add(lateralDashOrdini);
        pannelloOpzioni.add(lateralDashReport);
        pannelloOpzioni.setAlignmentX(CENTER_ALIGNMENT);
        pannelloOpzioni.setBorder(BorderFactory.createMatteBorder(-1, -1, -1, -1, new Color(19, 24, 40)));
        pannelloOpzioni.setMaximumSize(new Dimension(500, 750));
        pannellolaterale.add(pannelloOpzioni);

        pannellolaterale.setBorder(new EmptyBorder(10, 0, 0, 40)); // per dare un po di margini

        //************ CARD DASHBOARD ****************
        JPanel pannellodash = new JPanel();
        JPanel pannelloTab = new JPanel();

        // i pulsantoni della dashboard
        pannellodash.setBorder(new EmptyBorder(0, 20, 0, 0));
        pannellodash.setLayout(new GridLayout(3, 2, 50, 70));

        //Bottoni Dash
        button = new ButtonDash("TOTALE PRODOTTI IN MAGAZZINO");
        button.setBackground(new Color(26, 42, 79));
        pannellodash.add(button);

        button1 = new ButtonDash("TOTALE PRODOTTI IN ARRIVO");
        button1.setBackground(new Color(26, 42, 79));
        pannellodash.add(button1);

        button2 = new ButtonDash("SPESE TOTALI");
        button2.setBackground(new Color(26, 42, 79));
        pannellodash.add(button2);

        button3 = new ButtonDash("VENDITE TOTALI");
        button3.setBackground(new Color(26, 42, 79));
        pannellodash.add(button3);

        button4 = new ButtonDash("TOTALE UTENTI REGISTRATI");
        button4.setBackground(new Color(26, 42, 79));
        pannellodash.add(button4);

        button5 = new ButtonDash("ORDINI EFFETUATI");
        button5.setBackground(new Color(26, 42, 79));
        pannellodash.add(button5);
        
        
        VaiADash();  // Tieni premuto sulla schermata iniziale l'etichetta DashBoard

        //Le tabelle ...
        ProdottoDAO daop = new ProdottoDAO();

        JPanel TitoloTab1 = new JPanel(new GridLayout(1, 1));
        TitoloTab1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(66, 139, 221), new Color(66, 139, 221)), "prodotti in esaurimento", TitledBorder.RIGHT, TitledBorder.TOP));
        table = new JTable() {
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
        
        table.setFont(new Font("Arial", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.setEnabled(false);
        model = new DefaultTableModel();
        model.addColumn("SKU");
        model.addColumn("Nome");
        model.addColumn("Quantità");
        table.setModel(model);
        JScrollPane sp = new JScrollPane(table);
        TitoloTab1.add(sp);

        JPanel TitoloTab2 = new JPanel(new GridLayout(1, 1));
        TitoloTab2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, new Color(66, 139, 221), new Color(66, 139, 221)), "Prodotti in arrivo", TitledBorder.RIGHT, TitledBorder.TOP));
        table2 = new JTable(){
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
        table2.setFont(new Font("Arial", Font.PLAIN, 15));
        table2.setRowHeight(30);
        model2 = new DefaultTableModel();
        model2.addColumn("SKU");
        model2.addColumn("Nome");
        model2.addColumn("Quantità");
        model2.addColumn("#Ordine");
        model2.addColumn("Data arrivo");
        table2.setModel(model2);

        
        
        table2.setEnabled(false);

        JScrollPane sp2 = new JScrollPane(table2);
        TitoloTab2.add(sp2);

        OrdineDAO ordao = new OrdineDAO();
        ProdottoDAO prodao = new ProdottoDAO();

        try {
            for (Prodotto prod : daop.getAll()) {
                if (prod.getQty() <= prod.getQty_min()) {
                    
                    model.addRow(new Object[]{prod.getSku(), prod.getNome(), prod.getQty()});

                }

            }

            for (Ordine o : ordao.getAll()) {
                
                if (ordao.ggConsegnaPR2(o.getN_ordine(), o.getProdotto_sku()) <= 5 && o.getGiorni_alla_consegna() >= 0) {
                    Prodotto p = prodao.getBySku(o.getProdotto_sku());
                    model2.addRow(new Object[]{o.getProdotto_sku(), p.getNome(), o.getQty_in_arrivo(), o.getN_ordine(), ordao.dataArrivo(o.getN_ordine(), o.getProdotto_sku())});
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));

        } catch (ParseException ex) {
            Logger.getLogger("genlog").warning("ParseException\n" + StockManagement.printStackTrace(ex));
        }

        pannelloTab.setLayout(new GridLayout(2, 1));
        pannelloTab.add(TitoloTab1);
        pannelloTab.add(TitoloTab2);
        pannelloTab.setBorder(new EmptyBorder(0, 100, 0, 20));

        dashboard.setLayout(new GridLayout(1, 2));
        dashboard.add(pannellodash);
        dashboard.add(pannelloTab);

        add(pannellolaterale, BorderLayout.WEST);
        add(HomePanel, BorderLayout.CENTER);
        //X dar magini verticali...
        add(new JLabel("                           "), BorderLayout.NORTH); //Soluzioni rustiche
        add(new JLabel("                           "), BorderLayout.SOUTH); //Soluzioni rustiche
    }

    public void CreaMenu() {

        JMenuBar menu = new JMenuBar();
        JMenu m1 = new JMenu("Info");
        JMenu m2 = new JMenu("Profilo");
        JMenu m3 = new JMenu("Modalità");
        JMenu m4 = new JMenu("Tools");

        JMenuItem itemAbout = new JMenuItem("Manuale");
        itemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "\nGUIDA\n-Come creo un fornitore?\n-Come creo una categoria?\n-Come creo un prodotto?");
            }
        });
        m1.add(itemAbout);

        JMenuItem itemprofilo = new JMenuItem("Info Utente");
        JMenuItem itemAggiorna = new JMenuItem("Logout");
        JMenuItem itemChiudi = new JMenuItem("Chiudi");

        itemAggiorna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Riavvio programma ...");
                try {
                    riavviaStockManagement();
                } catch (IOException ex) {
                    Logger.getLogger("genlog").warning("IOException\n" + StockManagement.printStackTrace(ex));
                } catch (InterruptedException ex) {
                    Logger.getLogger("genlog").warning("InterruptedException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });

        itemChiudi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chiudiStockManagement();
                } catch (IOException ex) {
                    Logger.getLogger("genlog").warning("IOException\n" + StockManagement.printStackTrace(ex));
                } catch (InterruptedException ex) {
                    Logger.getLogger("genlog").warning("InterruptedException\n" + StockManagement.printStackTrace(ex));
                }

            }
        });

        m2.add(itemprofilo);
        m2.add(itemAggiorna);
        m2.add(itemChiudi);

        JMenuItem itemnegozio = new JMenuItem("Negozio");
        JMenuItem itemmagazzino = new JMenuItem("Magazzino");
        m3.add(itemnegozio);
        m3.add(itemmagazzino);

        JMenuItem itemSettings = new JMenuItem("Settings");
        JMenuItem itemuserlog = new JMenuItem("User Log");
        JMenuItem itemGenLog = new JMenuItem("Gen. Log");
        m4.add(itemSettings);
        m4.add(itemuserlog);
        m4.add(itemGenLog);

        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        menu.add(m4);
        setJMenuBar(menu);
    }

    
    

        public void disattivaTuttiIBottoniTranne(int cod) {
        for (int i = 0; i < pannelloOpzioni.getComponentCount(); i++) {

            if (i != cod) {
                ButtonLaterale b = (ButtonLaterale) pannelloOpzioni.getComponent(i);
                b.pan.setBackground(new Color(38, 44, 70));
                b.setBackground(new Color(38, 44, 70));
                b.icon.setIcon(ImpostaImg(b.pathfoto, 30));
                b.testo.setForeground(Color.white);
                b.premuto = false;
            }

        }

        }
        
        
    public void VaiADash(){
            
        // Schiaccia etichetta
        lateralDashDash.setBackground(new Color(19, 24, 40));
        lateralDashDash.premuto = true;
        lateralDashDash.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashDash.color_etichetta);
        String pathfotoa = lateralDashDash.pathfoto.replace(".", "_c.");
        lateralDashDash.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashDash.code);
        
        cardlayout.show(HomePanel, "Dashboard");
        
    }
    
    
    public void VaiAProdotti(String query) {
        
        // Schiaccia etichetta
        lateralDashProdotti.setBackground(new Color(19, 24, 40));
        lateralDashProdotti.premuto = true;
        lateralDashProdotti.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashProdotti.color_etichetta);
        String pathfotoa = lateralDashProdotti.pathfoto.replace(".", "_c.");
        lateralDashProdotti.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashProdotti.code);
        
        cardlayout.show(HomePanel, "Prodotti");
        prodotti.casella.setText(query);

    }

    public void VaiAProdottiInArrivo() {
        
        lateralDashProdotti.setBackground(new Color(19, 24, 40));
        lateralDashProdotti.premuto = true;
        lateralDashProdotti.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashProdotti.color_etichetta);
        String pathfotoa = lateralDashProdotti.pathfoto.replace(".", "_c.");
        lateralDashProdotti.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashProdotti.code);
                

        cardlayout.show(HomePanel, "Prodotti");
        prodotti.ViewOnlyInArrivo();

    }

    public void VaiAPreleva() {
        
        lateralDashOrdini.setBackground(new Color(19, 24, 40));
        lateralDashOrdini.premuto = true;
        lateralDashOrdini.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashOrdini.color_etichetta);
        String pathfotoa = lateralDashOrdini.pathfoto.replace(".", "_c.");
        lateralDashOrdini.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashOrdini.code);
        
        OrdiniStatus = true;
        
        ordini.refreshTab();
        cardlayout.show(HomePanel, "Preleva");
    }

    public void VaiAOrdini() {
        lateralDashOrdini.setBackground(new Color(19, 24, 40));
        lateralDashOrdini.premuto = true;
        lateralDashOrdini.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashOrdini.color_etichetta);
        String pathfotoa = lateralDashOrdini.pathfoto.replace(".", "_c.");
        lateralDashOrdini.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashOrdini.code);
        
        cardlayout.show(HomePanel, "Ordini");
    }

    public void VaiAOrdini(String forn) {

                lateralDashOrdini.setBackground(new Color(19, 24, 40));
        lateralDashOrdini.premuto = true;
        lateralDashOrdini.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashOrdini.color_etichetta);
        String pathfotoa = lateralDashOrdini.pathfoto.replace(".", "_c.");
        lateralDashOrdini.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashOrdini.code);
        cardlayout.show(HomePanel, "Ordini");

    }

    public void VaiAOrdiniconProdFORNULL(String forn, String prod) {
        
        lateralDashOrdini.setBackground(new Color(19, 24, 40));
        lateralDashOrdini.premuto = true;
        lateralDashOrdini.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashOrdini.color_etichetta);
        String pathfotoa = lateralDashOrdini.pathfoto.replace(".", "_c.");
        lateralDashOrdini.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashOrdini.code);
        
        if (ordiniadmin.controlloProdottoUguale(prod.substring(0, prod.indexOf("|")))) {
            JOptionPane.showMessageDialog(null, "non puoi associare più fornitori ad un solo prodotto mentre lo stai aggiungendo al carrello.");
            return;
        }

        cardlayout.show(HomePanel, "Ordini");
        ordiniadmin.aggiungiTOcarrello(prod, forn);

    }

    public void VaiAOrdiniconProdFornCEH(String prod) {
        
        lateralDashOrdini.setBackground(new Color(19, 24, 40));
        lateralDashOrdini.premuto = true;
        lateralDashOrdini.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashOrdini.color_etichetta);
        String pathfotoa = lateralDashOrdini.pathfoto.replace(".", "_c.");
        lateralDashOrdini.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashOrdini.code);
        
        cardlayout.show(HomePanel, "Ordini");
        ordiniadmin.casella.setText(prod);
    }

    public void VaiUtenti() {
        
        lateralDashAnagrafiche.setBackground(new Color(19, 24, 40));
        lateralDashAnagrafiche.premuto = true;
        lateralDashAnagrafiche.pan.setBackground(new Color(19, 24, 40));
        TitleLaterale.setForeground(lateralDashAnagrafiche.color_etichetta);
        String pathfotoa = lateralDashAnagrafiche.pathfoto.replace(".", "_c.");
        lateralDashAnagrafiche.icon.setIcon(ImpostaImg(pathfotoa, 30));
        
        disattivaTuttiIBottoniTranne(lateralDashAnagrafiche.code);
       
        cardlayout.show(HomePanel, "Anagrafiche");
        anagrafiche.ViewOnlyUtenti();
    }

    public void riavviaStockManagement() throws IOException, InterruptedException {

        System.out.println("STO PER RiaVVIARE");
        Connection con = null;

        try {
            con = DriverManagerConnectionPool.getConnection();
            System.out.println("La connessione: " + con);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getParent(), "Non trovo nessuna connesione :(");
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
        }

        try {
            DriverManagerConnectionPool.releaseConnection(con);

        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + ex);
        }

        System.out.println("La connessione dopo averla chiusa: " + con);
        dispose();
        StockManagement.main(new String[1]);
    }

    public void chiudiStockManagement() throws IOException, InterruptedException {

        System.out.println("STO PER CHIUDERE");
        Connection con = null;

        try {
            con = DriverManagerConnectionPool.getConnection();
            System.out.println("La connessione: " + con);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getParent(), "Non trovo nessuna connesione :(");
            Logger.getLogger("genlog").warning("SQLException:Non trovo nessuna connesione\n" + StockManagement.printStackTrace(ex));
        }

        try {
            DriverManagerConnectionPool.releaseConnection(con);

        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException" + StockManagement.printStackTrace(ex));
        }

        //  System.out.println("La connessione dopo averla chiusa: " + con);
        StockManagement.closeFH();
        dispose();
//        JavaProcessId.kILL();

    }

    class ButtonLaterale extends JPanel { //BOTTONI NAVIGAZIONE DASHBOARD/ANAGRAFICHE ECC

        public final String tipo;
        public boolean premuto = false; // per button nel pannello laterale
        public Color color_etichetta;
        private JLabel icon;
        private int code;
        private final JPanel pan;
        private final JPanel panetichetta;
        private final JLabel testo;
        private String pathfoto;

        public ButtonLaterale(String tipo) {
            super();
            this.tipo = tipo;
            super.setBackground(new Color(38, 44, 70));

            super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            
            if (tipo.equals("Dashboard")) {
                pathfoto = "/res/img/home.png";

                color_etichetta = new Color(73, 49, 145);
                code = 0;

            }
            if (tipo.equals("Ordini")) {
                pathfoto = "/res/img/ordini.png";
                color_etichetta = new Color(211, 213, 253);
                code = 5;

            }
            if (tipo.equals("Anagrafiche")) {
                pathfoto = "/res/img/anagrafiche.png";

                color_etichetta = new Color(228, 63, 90);
                code = 1;
            }

            if (tipo.equals("Categorie")) {
                pathfoto = "/res/img/categorie.png";
                color_etichetta = new Color(246,100,26);
                code = 2;
            }
            if (tipo.equals("Codici")) {
                pathfoto = "/res/img/codici.png";
                color_etichetta = new Color(56, 168, 127);
                code = 4;
            }

            if (tipo.equals("Prodotti")) {
                pathfoto = "/res/img/prodotti.png";
                color_etichetta = new Color(243, 169, 2);
                code = 3;
            }

            if (tipo.equals("Report")) {
                pathfoto = "/res/img/report.png";
                color_etichetta = new Color(29, 175, 215);
                code = 6;
            }

            
            panetichetta = new JPanel();
            panetichetta.setBackground(color_etichetta);
            
            pan = new JPanel();
            icon = new JLabel(ImpostaImg(pathfoto));
            icon.setBorder(new EmptyBorder(0, 20, 0, 20));
            pan.setBackground(new Color(38, 44, 70));
            testo = new JLabel(tipo);
            testo.setFont(new Font("Arial", Font.PLAIN, 18));
            testo.setBorder(new EmptyBorder(0, 20, 0, 30));
            pan.setLayout(new GridBagLayout());
            
            pan.add(testo);
            


            super.add(panetichetta);
            super.add(icon);
            super.add(pan);

            super.addMouseListener(new MouseListener() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    String pathfotoa = pathfoto.replace(".", "_a.");
                  
                     ButtonLaterale bottonepremuto = (ButtonLaterale) e.getSource();
                    if(!bottonepremuto.premuto) icon.setIcon(ImpostaImg(pathfotoa));  //Cambia in Accesso solo se non è premuto
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    ButtonLaterale bottonepremuto = (ButtonLaterale) e.getSource();
                    bottonepremuto.setBackground(new Color(19, 24, 40));
                    bottonepremuto.premuto = true;
                    pan.setBackground(new Color(19, 24, 40));
                    TitleLaterale.setForeground(color_etichetta);
                    String pathfotoa = pathfoto.replace(".", "_c.");
                    icon.setIcon(ImpostaImg(pathfotoa));


                    try {

                        if (user.getPermessi() == 0) {
                            codici.refreshTab();
                            prodotti.refreshTab();
                            categorie.refreshTab();
                            ordiniadmin.refreshTab();
                            

                            
                            
                        }
                        ordini.tabnomeprodotto.refresh();
                        refresh();

                    } catch (SQLException ex) {
                        Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                    }
                    disattivaTuttiIBottoniTranne(bottonepremuto.code);

                    if (tipo.equals("Ordini") && OrdiniStatus) {
                        cardlayout.show(HomePanel, "Preleva");
                    } else {
                        cardlayout.show(HomePanel, tipo);
                    }

                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ButtonLaterale bottonepremuto = (ButtonLaterale) e.getSource();
                    if (!bottonepremuto.premuto) {
                        testo.setForeground(Color.white);
                        icon.setIcon(ImpostaImg(pathfoto));

                    }

                }

            });

        }

        public void disattivaTuttiIBottoniTranne(int cod) {
            for (int i = 0; i < pannelloOpzioni.getComponentCount(); i++) {

                if (i != cod) {
                    ButtonLaterale b = (ButtonLaterale) pannelloOpzioni.getComponent(i);
                    b.pan.setBackground(new Color(38, 44, 70));
                    b.setBackground(new Color(38, 44, 70));
                    b.icon.setIcon(ImpostaImg(b.pathfoto));
                    b.testo.setForeground(Color.white);
                    b.premuto = false;
                }

            }

        }

        public ImageIcon ImpostaImg(String nomeImmag) {

            ImageIcon icon = new ImageIcon(getClass().getResource(nomeImmag));
            Image ImmagineScalata = icon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
            icon.setImage(ImmagineScalata);
            return icon;
        }

    }

    public void refresh() {
        ProdottoDAO daop = new ProdottoDAO();

        model.setRowCount(0);
        model2.setRowCount(0);

        OrdineDAO ordao = new OrdineDAO();
        ProdottoDAO prodao = new ProdottoDAO();


        try {
            for (Prodotto prod : daop.getAll()) {
                if (prod.getQty() <= prod.getQty_min()) {
                    model.addRow(new Object[]{prod.getSku(), prod.getNome(), prod.getQty()});
                }
            }

            for (Ordine o : ordao.getAll()) {
                if (ordao.ggConsegnaPR2(o.getN_ordine(), o.getProdotto_sku()) <= 5 && o.getGiorni_alla_consegna() >= 0) {
                    Prodotto p = prodao.getBySku(o.getProdotto_sku());
                    model2.addRow(new Object[]{o.getProdotto_sku(), p.getNome(), o.getQty_in_arrivo(), o.getN_ordine(), ordao.dataArrivo(o.getN_ordine(), o.getProdotto_sku())});
                }
                
                

            }
            //Aggiornare numeri sui pannelli della dash ...
            button.refreshButtonDash();
            button1.refreshButtonDash();
            button2.refreshButtonDash();
            button3.refreshButtonDash();
            button4.refreshButtonDash();
            button5.refreshButtonDash();

        } catch (SQLException ex) {
            Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
        } catch (ParseException ex) {
            Logger.getLogger("genlog").warning("ParseException\n" + StockManagement.printStackTrace(ex));
        }
    }

    class ButtonDash extends RoundedPanel { //PANNELLO TOTALE PRODOTTI

        private int number;
        public RoundedPanel vai;
        private String type;
        public JLabel title;
        private JLabel num;
        private JLabel scrittaVai;

        public ButtonDash(String type) {
            this.type = type;
            setForeground(new Color(19, 24, 40));
            number = 0;
            super.setLayout(new GridLayout(3, 1));
            super.setBackground(new Color(66, 139, 221));
            title = new JLabel(type); //Per dare ampiezza al jpanel
            title.setFont(new Font("Arial", Font.PLAIN, 20));
            title.setHorizontalAlignment(JLabel.CENTER);

            vai = new RoundedPanel();
            vai.setFont(new Font("Arial", Font.BOLD, 15));
            vai.setBackground(new Color(128, 128, 128));
            vai.setForeground(Color.black);
            vai.setLayout(new GridLayout(1, 1));
            num = new JLabel(); //Per dare ampiezza al jpanel

            refreshButtonDash();

            super.add(title);
            super.add(num);
            super.add(vai);

        }

        public void refreshButtonDash() {
            number = 0;

            if (type.equals("SPESE TOTALI")) {
                title.setForeground(new Color(29, 175, 215));
                vai.setBackground(new Color(29, 175, 215));
                scrittaVai = new JLabel(ImpostaImg("/res/img/report.png", 60));
                vai.removeAll();

                vai.add(scrittaVai);

            }

            if (type.equals("VENDITE TOTALI")) {
                title.setForeground(new Color(29, 175, 215));
                vai.setBackground(new Color(29, 175, 215));
                scrittaVai = new JLabel(ImpostaImg("/res/img/report.png", 60));
                vai.removeAll();

                vai.add(scrittaVai);

            }

            if (type.equals("TOTALE UTENTI REGISTRATI")) {
                title.setForeground(new Color(228, 63, 90));
                vai.setBackground(new Color(228, 63, 90));

                UtenteDAO dao = new UtenteDAO();
                try {
                    number = dao.getAll().size();
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }
                scrittaVai = new JLabel(ImpostaImg("/res/img/anagrafiche.png", 60));
                vai.removeAll();

                vai.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        VaiUtenti();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                vai.add(scrittaVai);

            }

            if (type.equals("TOTALE PRODOTTI IN MAGAZZINO")) {
                title.setForeground(new Color(243, 169, 2));
                vai.setBackground(new Color(243, 169, 2));

                ProdottoDAO dao = new ProdottoDAO();
                Enumeration names;
                String key;
                try {
                    names = dao.getCatAndSum().keys();
                    while (names.hasMoreElements()) {
                        key = (String) names.nextElement();
                        number += Integer.parseInt(dao.getCatAndSum().get(key));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }

                vai.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        VaiAProdotti("");
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                scrittaVai = new JLabel(ImpostaImg("/res/img/prodotti.png", 60));
                vai.removeAll();
                vai.add(scrittaVai);

            }

            if (type.equals("TOTALE PRODOTTI IN ARRIVO")) {

                title.setForeground(new Color(243, 169, 2));
                vai.setBackground(new Color(243, 169, 2));

                number = 0;
                OrdineDAO ordao = new OrdineDAO();
                try {
                    for (Ordine o : ordao.getAll()) {
                        if (o.getGiorni_alla_consegna() >= 0) {
                            number += o.getQty_in_arrivo();
                        }

                    }
                } catch (SQLException ex) {
                    Logger.getLogger("genlog").warning("SQLException\n" + StockManagement.printStackTrace(ex));
                }
                vai.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        VaiAProdottiInArrivo();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                scrittaVai = new JLabel(ImpostaImg("/res/img/prodotti.png", 60));
                vai.removeAll();
                vai.add(scrittaVai);

            }

            if (type.equals("ORDINI EFFETUATI")) {
                title.setForeground(new Color(211, 213, 253));
                vai.setBackground(new Color(211, 213, 253));

                Ordine o = new Ordine();
                number = o.leggiUltimoID();

                vai.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        /*if(OrdiniStatus) VaiAPreleva();  server x manenere vista "Preleva/ordini"
                        else */
                        OrdiniStatus = false;
                        VaiAOrdini("Seleziona un fornitore");
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                scrittaVai = new JLabel(ImpostaImg("/res/img/ordini.png", 60));
                vai.removeAll();

                vai.add(scrittaVai);

            }

            num.setText(String.valueOf(number));
            num.setFont(new Font("Arial", Font.BOLD, 30));
            num.setForeground(Color.white);
            num.setHorizontalAlignment(JLabel.CENTER);

        }

    }

    public ImageIcon ImpostaImg(String nomeImmag, int dim) {

        ImageIcon icon = new ImageIcon(getClass().getResource(nomeImmag));
        Image ImmagineScalata = icon.getImage().getScaledInstance(dim, dim, Image.SCALE_DEFAULT);
        icon.setImage(ImmagineScalata);
        return icon;
    }
}
