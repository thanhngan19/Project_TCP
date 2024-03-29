package GUI.Panel;
import java.awt.*;
import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import GUI.Dialog.KhuVucKhoDialog;
import GUI.Log_In;
import connect.ISocketClient;
import handler.IPhoneHandle;
import handler.IWareHouseHandle;
import handler.PhoneHandle;
import handler.WareHouseHandle;
import helper.JTableExporter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import GUI.Main;
import GUI.Component.IntegratedSearch;
import GUI.Component.MainFunction;
import javax.swing.border.EmptyBorder;
import GUI.Component.PanelBorderRadius;
import GUI.Component.itemTaskbar;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.util.List;

import model.ListTransfer;
import model.Phone;
import model.WareHouse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KhuVucKho extends JPanel implements ActionListener, ItemListener {
    PanelBorderRadius main, functionBar;
    JPanel contentCenter, right;
    JTable tableKhuvuc;
    JScrollPane scrollPane;
    JScrollPane scrollTableSanPham;
    MainFunction mainFunction;
    IntegratedSearch search;
    JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
    Color BackgroundColor = new Color(240, 247, 250);
    DefaultTableModel tblModel;
    Main m;
    private IWareHouseHandle ware= new WareHouseHandle();
    private IPhoneHandle phone = new PhoneHandle();
    private ISocketClient conn = new Log_In();

    public List<WareHouse> listKVK = ware.findAll();
    public List<Phone> listSP = phone.findAll();

    private void initComponent() {
        tableKhuvuc = new JTable();
        scrollTableSanPham = new JScrollPane();
        tblModel = new DefaultTableModel();
        String[] header = new String[]{"Mã kho", "Tên khu vực", "Ghi chú"};
        tblModel.setColumnIdentifiers(header);
        tableKhuvuc.setModel(tblModel);
        scrollTableSanPham.setViewportView(tableKhuvuc);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        TableColumnModel columnModel = tableKhuvuc.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(0).setPreferredWidth(2);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setCellRenderer(centerRenderer);
        tableKhuvuc.setFocusable(false);

        tableKhuvuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = tableKhuvuc.getSelectedRow();
                if (index != -1) {
                    List<Phone> listSP = phone.selectPhoneByWare(listKVK.get(index).getId());
                    ListCustomersInDePot(listSP);
                }
            }
        });

        this.setBackground(BackgroundColor);
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setOpaque(true);

        // pnlBorder1, pnlBorder2, pnlBorder3, pnlBorder4 chỉ để thêm contentCenter ở giữa mà contentCenter không bị dính sát vào các thành phần khác
        contentCenter = new JPanel();
        contentCenter.setPreferredSize(new Dimension(1100, 600));
        contentCenter.setBackground(BackgroundColor);
        contentCenter.setLayout(new BorderLayout(10, 10));
        this.add(contentCenter);

        // functionBar là thanh bên trên chứa các nút chức năng như thêm xóa sửa, và tìm kiếm
        functionBar = new PanelBorderRadius();
        functionBar.setPreferredSize(new Dimension(0, 100));
        functionBar.setLayout(new GridLayout(1, 2, 50, 0));
        functionBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] action = {"create", "update", "delete"};
        mainFunction = new MainFunction(m.user.getManhomquyen().getId(), "khuvuckho", action);
        for (String ac : action) {
            mainFunction.btn.get(ac).addActionListener(this);
        }
        functionBar.add(mainFunction);

        search = new IntegratedSearch(new String[]{"Tất cả", "Mã khu vực kho", "Tên khu vực kho"});
        search.cbxChoose.addItemListener(this);
        search.txtSearchForm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        search.btnReset.addActionListener(this);
        functionBar.add(search);
        contentCenter.add(functionBar, BorderLayout.NORTH);
        // main là phần ở dưới để thống kê bảng biểu
        main = new PanelBorderRadius();
        BoxLayout boxly = new BoxLayout(main, BoxLayout.Y_AXIS);
        main.setLayout(boxly);
//        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentCenter.add(main, BorderLayout.CENTER);
        main.add(scrollTableSanPham);

        right = new JPanel();
        right.setBackground(BackgroundColor);
        right.setLayout(new FlowLayout(0, 4, 10));
        right.setPreferredSize(new Dimension(400, 800));
        JLabel tit = new JLabel("Danh sách sản phẩm trong kho");
        tit.setFont(new java.awt.Font(FlatRobotoFont.FAMILY, 1, 16));
        right.add(tit);
//        right.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sản phẩm trong kho", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14)));
        scrollPane = new JScrollPane(right, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setBackground(BackgroundColor);
        contentCenter.add(scrollPane, BorderLayout.EAST);
    }

    public KhuVucKho(Main m) {
        this.m = m;
        initComponent();
//        tableKhuvuc.setDefaultEditor(Object.class, null);
        loadDataTable(listKVK);
    }

    public void loadDataTable(List<WareHouse> result) {
        int n =1;
        tblModel.setRowCount(0);
        for (WareHouse kvk : result) {
            tblModel.addRow(new Object[]{
                    n++, kvk.getName(), kvk.getNote()
            });
        }
    }



    public void ListCustomersInDePot(List<Phone> result) {
        right.removeAll();
        JLabel tit = new JLabel("Danh sách sản phẩm đang có ở khu vực");
        tit.setFont(new java.awt.Font(FlatRobotoFont.FAMILY, 1, 16));
        right.add(tit);
        itemTaskbar listItem[] = new itemTaskbar[result.size()];
        int i = 0;
        for (Phone sp : result) {
            if (sp.getQuantity() != 0) {
                listItem[i] = new itemTaskbar(sp.getImg(), sp.getName(), sp.getQuantity());
                right.add(listItem[i]);
                i++;
            }
        }

        if (i == 0) {
            if (result.isEmpty()) {
                JLabel lblIcon = new JLabel("Không có sản phẩm");
                lblIcon.setPreferredSize(new Dimension(380, 300));
                lblIcon.setIcon(new FlatSVGIcon("./icon/null.svg"));
                lblIcon.setHorizontalTextPosition(SwingConstants.CENTER);
                lblIcon.setVerticalTextPosition(SwingConstants.TOP);
                right.add(lblIcon);
            }
        }
        right.repaint();
        right.validate();
    }

    public int getRowSelected() {
        int index = tableKhuvuc.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khu vực kho");
        }
        return index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mainFunction.btn.get("create")) {
            KhuVucKhoDialog kvkDialog = new KhuVucKhoDialog(this, owner, "Thêm khu vực kho", true, "create");
        } else if (e.getSource() == mainFunction.btn.get("update") && listKVK.get(getRowSelected()).getStatus()==1 ) {
            int index = getRowSelected();
            if (index != -1) {
                if(listKVK.get(getRowSelected()).getStatus()==1){
                    KhuVucKhoDialog kvkDialog = new KhuVucKhoDialog(this, owner, "Chỉnh sửa khu vực kho", true, "update", listKVK.get(index));
                }else{
                    JOptionPane.showMessageDialog(this, "Bạn không thể sửa ");
                }
            }
        } else if (e.getSource() == mainFunction.btn.get("delete")) {
            int index = getRowSelected();
            if (index != -1) {
                int input = JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc chắn muốn xóa khu vực!", "Xóa khu vực kho",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (input == 0) {
                    int check = 0;
                    for (Phone i : listSP) {
                        if (listKVK.get(index).getId() == i.getWareHouse().getId()) {
                            check++;
                            break;
                        }
                    }
                    if (check == 0) {
                        boolean checkTu=false;
                        for(Phone item :listSP){
                            if(item.getWareHouse().getId() ==listKVK.get(index).getId()){
                                checkTu=true;
                            }
                        }
                        if(checkTu){
                            JOptionPane.showMessageDialog(this, "Không thể xóa khu vực này !!");
                        }
                        else{
                            ware.deletePhone(new ListTransfer("delete",listKVK.get(index)));
                            listKVK= conn.findAll().getListWare();
                            loadDataTable(listKVK);
                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Không thể xóa kho vì vẫn còn sản phẩm trong kho.");
                    }
                }
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}