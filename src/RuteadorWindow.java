import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * Created by JFormDesigner on Wed Sep 10 11:52:49 CST 2014
 */

/**
 * @author Werner
 */
public class RuteadorWindow extends JFrame  {

    final static boolean DEBUG = true;

    private final String hostAddress;
    JFileChooser fd = new JFileChooser();
    StopRouter actionStopRouter;
    StopForwarder actionStopForwarder;
    ScheduledThreadPoolExecutor exec;
    public static DialogSettings dlgSettings = new DialogSettings();

    public void log(final String txt) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (txtLog) {
                    txtLog.append(txt);
                }
            }
        });
    }

    public void println(final String txt) {
        print(txt + "\n");
    }

    public void print(final String txt) {
        log(txt);
    }

    public RuteadorWindow(String hostAddress) {

        super();
        this.hostAddress = hostAddress;

        try {
            // set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // set icon image
            Image image = ImageIO.read(ClassLoader.getSystemResource("images/router.png"));
            super.setIconImage(image);

            // register font
            Font font = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("images/terminal.TTF"));
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

        } catch (Exception ex) {
            Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        }

        initComponents();

        routeTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
//        MaskFormatter mf1 = null;
//        try {
//            mf1 = new MaskFormatter("###.###.###.###");
//        } catch (ParseException e) {
//
//        }
//        mf1.setPlaceholderCharacter('_');
//
//        mf1.install(txtTarget);

        setSize(new Dimension(800, 600));

        lblSource.setText(Setup.ROUTER_NAME + " (" + hostAddress + ")");
        setTitle("Proyecto 2 CC8 - Ruteador de Paquetes - " + Setup.ROUTER_NAME + " (" + hostAddress + ")");

        exec = new ScheduledThreadPoolExecutor(1);

        println("CONSOLA DE MENSAJES");
        println("-------------------");
        println("");

        abrirLog();

        this.actionRouteTable.actionPerformed(null);
        setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panelMenu = new JPanel();
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        menu2 = new JMenu();
        menuItem4 = new JMenuItem();
        menuItem5 = new JMenuItem();
        menuItem6 = new JMenuItem();
        menuItem3 = new JMenuItem();
        menu3 = new JMenu();
        menuItem2 = new JMenuItem();
        toolBar1 = new JToolBar();
        button2 = new JButton();
        button8 = new JButton();
        btnTable = new JButton();
        panelStatus = new JPanel();
        pnlStatus3 = new JPanel();
        labelStatus1 = new JLabel();
        pnlStatus2 = new JPanel();
        labelStatus2 = new JLabel();
        panelMain = new JPanel();
        panelMsg = new JPanel();
        label3 = new JLabel();
        lblSource = new JLabel();
        label4 = new JLabel();
        txtTarget = new JTextField();
        label5 = new JLabel();
        scrollPane1 = new JScrollPane();
        txtMsg = new JTextArea();
        panel1 = new JPanel();
        btnSend = new JButton();
        panel2 = new JPanel();
        splitPane1 = new JSplitPane();
        scrollPane2 = new JScrollPane();
        txtLog = new JTextArea();
        contentPanel = new JPanel();
        toolBar2 = new JToolBar();
        button3 = new JButton();
        button6 = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        button7 = new JButton();
        label1 = new JLabel();
        scrollPane3 = new JScrollPane();
        routeTable = new JTable();
        actionSalir = new Salir();
        actionRouteTable = new ShowRouteTable();
        actionAbout = new About();
        actionStartRouter = new StartRouter();
        actionSendMessage = new SendMessage();
        actionStartForwarder = new StartForwarder();
        actionSettings = new Settings();

        //======== this ========
        setTitle("Proyecto 2 CC8 - Ruteador de Paquetes");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setLocationByPlatform(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panelMenu ========
        {
            panelMenu.setBorder(null);
            panelMenu.setLayout(new GridLayout(2, 1));

            //======== menuBar1 ========
            {

                //======== menu1 ========
                {
                    menu1.setText("Archivo");
                    menu1.setMnemonic('A');

                    //---- menuItem1 ----
                    menuItem1.setMnemonic('S');
                    menuItem1.setAction(actionSalir);
                    menu1.add(menuItem1);
                }
                menuBar1.add(menu1);

                //======== menu2 ========
                {
                    menu2.setText("Herramientas");
                    menu2.setMnemonic('H');

                    //---- menuItem4 ----
                    menuItem4.setText("text");
                    menuItem4.setAction(actionStartRouter);
                    menu2.add(menuItem4);

                    //---- menuItem5 ----
                    menuItem5.setText("text");
                    menuItem5.setAction(actionStartForwarder);
                    menu2.add(menuItem5);
                    menu2.addSeparator();

                    //---- menuItem6 ----
                    menuItem6.setAction(actionSettings);
                    menu2.add(menuItem6);

                    //---- menuItem3 ----
                    menuItem3.setAction(actionRouteTable);
                    menuItem3.setMnemonic('T');
                    menu2.add(menuItem3);
                }
                menuBar1.add(menu2);

                //======== menu3 ========
                {
                    menu3.setText("Ayuda");
                    menu3.setMnemonic('U');

                    //---- menuItem2 ----
                    menuItem2.setAction(actionAbout);
                    menu3.add(menuItem2);
                }
                menuBar1.add(menu3);
            }
            panelMenu.add(menuBar1);

            //======== toolBar1 ========
            {
                toolBar1.setFloatable(false);

                //---- button2 ----
                button2.setAction(actionStartRouter);
                button2.setMnemonic('I');
                toolBar1.add(button2);

                //---- button8 ----
                button8.setAction(actionStartForwarder);
                button8.setMnemonic('I');
                toolBar1.add(button8);

                //---- btnTable ----
                btnTable.setText("text");
                btnTable.setAction(actionRouteTable);
                btnTable.setMnemonic('T');
                btnTable.setSelected(true);
                toolBar1.add(btnTable);
            }
            panelMenu.add(toolBar1);
        }
        contentPane.add(panelMenu, BorderLayout.NORTH);

        //======== panelStatus ========
        {
            panelStatus.setBorder(new EmptyBorder(2, 2, 2, 2));
            panelStatus.setLayout(new GridBagLayout());
            ((GridBagLayout)panelStatus.getLayout()).columnWidths = new int[] {170, 0, 0};
            ((GridBagLayout)panelStatus.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panelStatus.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout)panelStatus.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== pnlStatus3 ========
            {
                pnlStatus3.setBorder(new CompoundBorder(
                    new EtchedBorder(),
                    new EmptyBorder(3, 3, 3, 3)));
                pnlStatus3.setLayout(new BorderLayout(5, 5));

                //---- labelStatus1 ----
                labelStatus1.setText("Listo");
                pnlStatus3.add(labelStatus1, BorderLayout.CENTER);
            }
            panelStatus.add(pnlStatus3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //======== pnlStatus2 ========
            {
                pnlStatus2.setBorder(new CompoundBorder(
                    new EtchedBorder(),
                    new EmptyBorder(3, 3, 3, 3)));
                pnlStatus2.setLayout(new BorderLayout(5, 5));

                //---- labelStatus2 ----
                labelStatus2.setText("Listo");
                pnlStatus2.add(labelStatus2, BorderLayout.CENTER);
            }
            panelStatus.add(pnlStatus2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panelStatus, BorderLayout.SOUTH);

        //======== panelMain ========
        {
            panelMain.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 5, 0),
                new EtchedBorder()));
            panelMain.setLayout(new BorderLayout(5, 5));

            //======== panelMsg ========
            {
                panelMsg.setBorder(new EmptyBorder(5, 5, 5, 5));
                panelMsg.setLayout(new GridBagLayout());
                ((GridBagLayout)panelMsg.getLayout()).columnWidths = new int[] {96, 0, 0};
                ((GridBagLayout)panelMsg.getLayout()).rowHeights = new int[] {0, 0, 64, 0, 0};
                ((GridBagLayout)panelMsg.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout)panelMsg.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

                //---- label3 ----
                label3.setText("Origen:");
                label3.setFont(new Font("Tahoma", Font.BOLD, 14));
                panelMsg.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- lblSource ----
                lblSource.setText("localhost");
                lblSource.setFont(new Font("Tahoma", Font.BOLD, 14));
                panelMsg.add(lblSource, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- label4 ----
                label4.setText("Destino:");
                label4.setFont(new Font("Tahoma", Font.BOLD, 14));
                label4.setDisplayedMnemonic('D');
                panelMsg.add(label4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- txtTarget ----
                txtTarget.setFont(new Font("Tahoma", Font.PLAIN, 14));
                txtTarget.setFocusAccelerator('D');
                panelMsg.add(txtTarget, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- label5 ----
                label5.setText("Mensaje:");
                label5.setFont(new Font("Tahoma", Font.BOLD, 14));
                label5.setVerticalAlignment(SwingConstants.TOP);
                label5.setDisplayedMnemonic('M');
                panelMsg.add(label5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //======== scrollPane1 ========
                {

                    //---- txtMsg ----
                    txtMsg.setText("Mensaje de prueba");
                    txtMsg.setFocusAccelerator('M');
                    scrollPane1.setViewportView(txtMsg);
                }
                panelMsg.add(scrollPane1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== panel1 ========
                {
                    panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));

                    //---- btnSend ----
                    btnSend.setAction(actionSendMessage);
                    btnSend.setMnemonic('E');
                    panel1.add(btnSend);
                }
                panelMsg.add(panel1, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            panelMain.add(panelMsg, BorderLayout.PAGE_START);

            //======== panel2 ========
            {
                panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
                panel2.setLayout(new BorderLayout(5, 5));

                //======== splitPane1 ========
                {
                    splitPane1.setDividerLocation(540);

                    //======== scrollPane2 ========
                    {

                        //---- txtLog ----
                        txtLog.setEditable(false);
                        txtLog.setBackground(Color.black);
                        txtLog.setForeground(Color.white);
                        txtLog.setLineWrap(true);
                        scrollPane2.setViewportView(txtLog);
                    }
                    splitPane1.setLeftComponent(scrollPane2);

                    //======== contentPanel ========
                    {
                        contentPanel.setLayout(new BorderLayout());

                        //======== toolBar2 ========
                        {
                            toolBar2.setFloatable(false);

                            //---- button3 ----
                            button3.setVisible(false);
                            toolBar2.add(button3);

                            //---- button6 ----
                            button6.setVisible(false);
                            toolBar2.add(button6);
                            toolBar2.addSeparator();

                            //---- button4 ----
                            button4.setVisible(false);
                            toolBar2.add(button4);

                            //---- button5 ----
                            button5.setVisible(false);
                            toolBar2.add(button5);
                            toolBar2.addSeparator();

                            //---- button7 ----
                            button7.setVisible(false);
                            toolBar2.add(button7);

                            //---- label1 ----
                            label1.setText("Routers Adyacentes");
                            label1.setHorizontalAlignment(SwingConstants.LEFT);
                            label1.setFont(label1.getFont().deriveFont(Font.BOLD));
                            label1.setPreferredSize(new Dimension(3000, 14));
                            label1.setHorizontalTextPosition(SwingConstants.RIGHT);
                            label1.setMaximumSize(new Dimension(3000, 14));
                            toolBar2.add(label1);
                        }
                        contentPanel.add(toolBar2, BorderLayout.PAGE_START);

                        //======== scrollPane3 ========
                        {

                            //---- routeTable ----
                            routeTable.setModel(new DefaultTableModel(
                                new Object[][] {
                                },
                                new String[] {
                                    "#", "Nombre", "IP", "Costo"
                                }
                            ) {
                                Class<?>[] columnTypes = new Class<?>[] {
                                    Integer.class, String.class, String.class, Integer.class
                                };
                                boolean[] columnEditable = new boolean[] {
                                    false, false, false, false
                                };
                                @Override
                                public Class<?> getColumnClass(int columnIndex) {
                                    return columnTypes[columnIndex];
                                }
                                @Override
                                public boolean isCellEditable(int rowIndex, int columnIndex) {
                                    return columnEditable[columnIndex];
                                }
                            });
                            {
                                TableColumnModel cm = routeTable.getColumnModel();
                                cm.getColumn(0).setMaxWidth(30);
                                cm.getColumn(0).setPreferredWidth(30);
                            }
                            routeTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
                            routeTable.setRowHeight(20);
                            routeTable.setRowMargin(2);
                            scrollPane3.setViewportView(routeTable);
                        }
                        contentPanel.add(scrollPane3, BorderLayout.CENTER);
                    }
                    splitPane1.setRightComponent(contentPanel);
                }
                panel2.add(splitPane1, BorderLayout.CENTER);
            }
            panelMain.add(panel2, BorderLayout.CENTER);
        }
        contentPane.add(panelMain, BorderLayout.CENTER);
        setSize(770, 415);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        actionStopRouter = new StopRouter();
        actionStopForwarder = new StopForwarder();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panelMenu;
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JMenu menu2;
    private JMenuItem menuItem4;
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JMenuItem menuItem3;
    private JMenu menu3;
    private JMenuItem menuItem2;
    private JToolBar toolBar1;
    private JButton button2;
    private JButton button8;
    private JButton btnTable;
    private JPanel panelStatus;
    private JPanel pnlStatus3;
    private JLabel labelStatus1;
    private JPanel pnlStatus2;
    private JLabel labelStatus2;
    private JPanel panelMain;
    private JPanel panelMsg;
    private JLabel label3;
    private JLabel lblSource;
    private JLabel label4;
    private JTextField txtTarget;
    private JLabel label5;
    private JScrollPane scrollPane1;
    private JTextArea txtMsg;
    private JPanel panel1;
    private JButton btnSend;
    private JPanel panel2;
    private JSplitPane splitPane1;
    private JScrollPane scrollPane2;
    private JTextArea txtLog;
    private JPanel contentPanel;
    private JToolBar toolBar2;
    private JButton button3;
    private JButton button6;
    private JButton button4;
    private JButton button5;
    private JButton button7;
    private JLabel label1;
    private JScrollPane scrollPane3;
    private JTable routeTable;
    private Salir actionSalir;
    private ShowRouteTable actionRouteTable;
    private About actionAbout;
    private StartRouter actionStartRouter;
    private SendMessage actionSendMessage;
    private StartForwarder actionStartForwarder;
    private Settings actionSettings;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class Salir extends AbstractAction {
        private Salir() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Salir");
            putValue(SHORT_DESCRIPTION, "Salir el programa");
            putValue(LONG_DESCRIPTION, "Salir el programa");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
            putValue(ACTION_COMMAND_KEY, "Salir");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/exclamation.png")));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class ShowRouteTable extends AbstractAction {
        private ShowRouteTable() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Tabla de Ruteo");
            putValue(SHORT_DESCRIPTION, "Mostrar Tabla de Ruteo");
            putValue(LONG_DESCRIPTION, "Mostrar Tabla de Ruteo");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/table.png")));
            putValue(ACTION_COMMAND_KEY, "RouteTable");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            btnTable.setSelected(!btnTable.isSelected());
            contentPanel.setVisible(btnTable.isSelected());
            splitPane1.setDividerSize(contentPanel.isVisible() ? 5 : 0);
            splitPane1.updateUI();
        }
    }

    private class About extends AbstractAction {
        private About() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Acerca de...");
            putValue(SHORT_DESCRIPTION, "Acerca de...");
            putValue(LONG_DESCRIPTION, "Acerca de...");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/help.png")));
            putValue(ACTION_COMMAND_KEY, "About");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,
                    "CC8 - Implementando un Router\n" +
                            "Integrantes de grupo:\n" +
                            "Werner Garcia\n" +
                            "Asdrubal Batz\n" +
                            "Jose Vasquez",
                    "Acerca de...", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class OpenRouteFile extends AbstractAction {
        private OpenRouteFile() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if (fd.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                abrirLog();
            }
        }
    }

    private void abrirLog() {
        routeTable.setModel(new MyTableModel());
        initColumnSizes(routeTable);
        actionStartRouter.setEnabled(true);
        actionStartForwarder.setEnabled(true);
        actionSendMessage.setEnabled(true);
    }

    private void initColumnSizes(JTable table) {
        TableColumn column = null;
        for (int i = 0; i < 3; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0 || i == 2) {
                column.setPreferredWidth(30);
            } else {
                column.setPreferredWidth(100); //second column is bigger
            }
        }

    }

    private class MyTableModel extends AbstractTableModel {

        private final String[] columnNames = {"#",
                "Nombre",
                "IP",
                "Costo"};

        private final ArrayList<NbrCostPair> data;

        public MyTableModel() {
            data = new ArrayList<NbrCostPair>(Setup.nbrList);
            refreshRoutes();
        }

        public void refreshRoutes() {
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            switch(col){
                case 0: return row+1;
                case 1: return data.get(row).getNbr().getId();
                case 2: return data.get(row).getNbr().getAddr().getHostAddress();
                case 3: return data.get(row).getCost();
            }
            return null;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public ArrayList<NbrCostPair> getData() {
            return data;
        }

    }

    private class SendMessage extends AbstractAction {
        private SendMessage() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Enviar mensaje");
            putValue(SHORT_DESCRIPTION, "Enviar mensaje");
            putValue(LONG_DESCRIPTION, "Enviar mensaje");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/email_go.png")));
            putValue(ACTION_COMMAND_KEY, "SendMsg");
            setEnabled(false);
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

       // Pattern pat = Pattern.compile("\\d{1,4}\\.\\d{1,4}\\.\\d{1,4}\\.\\d{1,4}");
        public void actionPerformed(ActionEvent e) {
            String target = txtTarget.getText();
           // target.replaceAll("_", "");
         //   Matcher matcher = pat.matcher(target);
//            if (!matcher.matches()) {
//                JOptionPane.showMessageDialog(null, "La direccion IP es invalida!");
//                return;
//            }
            if (!RoutingService.dv.containsKey(target)) {
                JOptionPane.showMessageDialog(null, "La ruta destino es invalida!");
                return;
            }
            if (ForwardingService.SendMessage(new ForwarderMessage(
                    lblSource.getText(),
                    target,
                    txtMsg.getText()))){
                labelStatus2.setText("El mensaje fue enviado a " + target);
            }
        }
    }

    private class StartForwarder extends AbstractAction {
        private StartForwarder() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Iniciar Forwarder");
            putValue(SHORT_DESCRIPTION, "Iniciar Forwarder");
            putValue(LONG_DESCRIPTION, "Iniciar Forwarder");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/resultset_nextfw.png")));
            putValue(ACTION_COMMAND_KEY, "StartForwarder");
            setEnabled(false);
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            actionStartForwarder.setEnabled(false);
            ForwardingService.start(Setup.address, Setup.FORWARDING_PORT); // iniciar servidor de mensajes

            exec.schedule(new Runnable() {
                public void run() {
                    if (ForwardingService.isServerRunning()) {
                        button8.setAction(actionStopForwarder);
                        menuItem5.setAction(actionStopForwarder);
                    }
                    actionStartForwarder.setEnabled(true);
                }
            }, 1, TimeUnit.SECONDS);

        }
    }

    private class StartRouter extends AbstractAction {
        private StartRouter() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Iniciar Router");
            putValue(SHORT_DESCRIPTION, "Iniciar Router");
            putValue(LONG_DESCRIPTION, "Iniciar Router");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/resultset_next.png")));
            putValue(ACTION_COMMAND_KEY, "StartRouter");
            setEnabled(false);
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            actionStartRouter.setEnabled(false);
            RoutingService.start(); // iniciar servidor de mensajes

            exec.schedule(new Runnable() {
                public void run() {
                    if (RoutingService.isServerRunning()) {
                        button2.setAction(actionStopRouter);
                        menuItem4.setAction(actionStopRouter);
                    }
                    actionStartRouter.setEnabled(true);
                }
            }, 1, TimeUnit.SECONDS);


        }
    }

    private class StopRouter extends AbstractAction {
        private StopRouter() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Detener Router");
            putValue(SHORT_DESCRIPTION, "Detener Router");
            putValue(LONG_DESCRIPTION, "Detener Router");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/resultset_stop.png")));
            putValue(ACTION_COMMAND_KEY, "StopRouter");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            RoutingService.stop();
            button2.setAction(actionStartRouter);
            menuItem4.setAction(actionStartRouter);
        }
    }

    private class StopForwarder extends AbstractAction {
        private StopForwarder() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Detener Forwarder");
            putValue(SHORT_DESCRIPTION, "Detener Forwarder");
            putValue(LONG_DESCRIPTION, "Detener Forwarder");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/resultset_stopfw.png")));
            putValue(ACTION_COMMAND_KEY, "StopForwarder");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            ForwardingService.stop();
            button8.setAction(actionStartForwarder);
            menuItem5.setAction(actionStartForwarder);
        }
    }

    private class Settings extends AbstractAction {
        private Settings() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Preferencias");
            putValue(SHORT_DESCRIPTION, "Abrir ventana de preferencias");
            putValue(LONG_DESCRIPTION, "Abrir ventana de preferencias");
            putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/images/settings.png")));
            putValue(ACTION_COMMAND_KEY, "Settings");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dlgSettings.setVisible(true);
        }
    }
}
