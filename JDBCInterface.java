package Assignment10.src;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class JDBCInterface extends JFrame {
	private JPanel controlPanel;
	private JTextArea textQueryArea;
	private JTextField lastNameQuery;
	private JButton queryButton;
	private JLabel lastName, firstName, age, city;
	private JTextField forLastName, forFirstName, forAge, forCity;

	private Connection conn;
	private PreparedStatement queryStmtLastName;
	
	
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	final int AREA_ROWS = 20;
	final int AREA_COLUMNS = 40;
   
   public JDBCInterface() {

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:assignment.db");
			queryStmtLastName = conn.prepareStatement("Select * from People WHERE Last = ?");

		} catch (SQLException e) {
			System.err.println("Connection error: " + e);
			System.exit(1);
		}
		
	   createControlPanel();
	   queryButton.addActionListener(new QueryButtonListener());

	   textQueryArea = new JTextArea(
	            AREA_ROWS, AREA_COLUMNS);
	   textQueryArea.setEditable(false);
	   
	   /* scrollPanel is optional */
	   JScrollPane scrollPane = new JScrollPane(textQueryArea);
	   JPanel textPanel = new JPanel();
	   textPanel.add(scrollPane);
	   this.add(textPanel, CENTER);
	   this.add(controlPanel, NORTH);
   }
   
   private JPanel createControlPanel() {
	   
	   /* you are going to have to create a much more fully-featured layout */
	   
	   controlPanel = new JPanel();

	   //add some more
	   JButton insetButton = new JButton("Insert");


	   GridLayout layout = new GridLayout(0,4,10,10);
	   JPanel upPanel = new JPanel(layout);
	   lastName = new JLabel("Last name: ");
	   lastName.setHorizontalAlignment(JLabel.RIGHT);
	   firstName = new JLabel("First name: ");
	   firstName.setHorizontalAlignment(JLabel.RIGHT);
	   forLastName = new JTextField();
	   forFirstName = new JTextField();
	   upPanel.add(lastName);
	   upPanel.add(forLastName);
	   upPanel.add(firstName);
	   upPanel.add(forFirstName);

	   age = new JLabel("Age: ");
	   age.setHorizontalAlignment(JLabel.RIGHT);
	   city = new JLabel("City: ");
	   city.setHorizontalAlignment(JLabel.RIGHT);
	   forAge = new JTextField();
	   forCity = new JTextField();
	   upPanel.add(age);
	   upPanel.add(forAge);
	   upPanel.add(city);
	   upPanel.add(forCity);

	   upPanel.add(new JLabel(""));
	   upPanel.add(insetButton);
	   upPanel.add(new JLabel(""));
	   upPanel.add(new JLabel(""));
	   //my code ends

	   JLabel lbl = new JLabel("Last Name:");
	   lbl.setHorizontalAlignment(JLabel.RIGHT);
	   upPanel.add(lbl);
	   lastNameQuery = new JTextField();
	   upPanel.add(lastNameQuery);

	   queryButton = new JButton("Execute Query");
	   upPanel.add(queryButton);

	   controlPanel.add(upPanel);  //added
	   insetButton.addActionListener(new InsertButtonListener());  //added
	   queryButton.addActionListener(new QueryButtonListener()); //added

	   return controlPanel;
   }
   
   class InsertButtonListener implements ActionListener {
	   public void actionPerformed(ActionEvent event) {
		   /* You will have to implement this */
		   	String fn = forFirstName.getText();
		   	String ln = forLastName.getText();
		   	String a = forAge.getText();
		   String c = forCity.getText();
		   	if(fn.length()<1 || ln.length()<1 || a.length()<1 ||c.length()<1){
				JOptionPane.showMessageDialog(null, "All Fields must be filled","Message",JOptionPane.WARNING_MESSAGE);
			}else{
				try {
					String queryString = "INSERT INTO People (Last, First, age, city)  VALUES (?,?,?,?)";
					PreparedStatement stmt = conn.prepareStatement(queryString);
					stmt.setString(1,fn);
					stmt.setString(2,ln);
					stmt.setInt(3,Integer.parseInt(forAge.getText()));
					stmt.setString(4,forCity.getText());
					int x= stmt.executeUpdate();
					if(x>0){
						String rowString = "";
						String colName="";
						System.out.println("INSERT query has been successfully executed!!");
						forFirstName.setText("");
						forLastName.setText("");
						forCity.setText("");
						forAge.setText("");


						//insert into the list, then the text area show nothing
						textQueryArea.setText(colName + "\n" + rowString);
					}else{
						System.out.println("Failed executing INSERT query!!");
					}
				}catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
	   }
   }
   
   class QueryButtonListener implements ActionListener {
	   public void actionPerformed(ActionEvent event) {
		   /* as far as the columns, it is totally acceptable to
			* get all of the column data ahead of time, so you only
			* have to do it once, and just reprint the string
			* in the text area.
			*/

		   /* you want to change things here so that if the text of the
			* last name query field is empty, it should query for all rows.
			*
			* For now, if the last name query field is blank, it will execute:
			* SELECT * FROM People WHERE Last=''
			* which will give no results
			*/
		   try {
			   textQueryArea.setText("");
			   PreparedStatement stmt = queryStmtLastName;
			   String lastNameText = lastNameQuery.getText();
			   String rowString = "";
			   String colName="";

			   if (lastNameText.isEmpty()) {
			   	   Statement sta = conn.createStatement();
				   ResultSet rs = sta.executeQuery("select * from People");
				   ResultSetMetaData rsd = rs.getMetaData();
				   colName = "First\t" + "Last\t" + "age\t" + "city\t" + "id\t";

				   int numColumns = rsd.getColumnCount();
				   while (rs.next()) {
					   for (int i = 1; i <= numColumns; i++) {
						   Object o = rs.getObject(i);
						   rowString += o.toString() + "\t";
						//   System.out.print(rs.getString(i) + " ");
					   }
					   rowString += "\n";
				   }
//				   System.out.println("numcolumns is " + numColumns);
//				   System.out.println("rowString  is  \n" + rowString);
			   }else{
				   stmt.setString(1, lastNameText);
				   ResultSet rset = stmt.executeQuery();
				   ResultSetMetaData rsmd = rset.getMetaData();
			   	colName = "First\t" + "Last\t" + "age\t" + "city\t" + "id\t";
				   int numColumns = rsmd.getColumnCount();
			   	while (rset.next()) {
				   for (int i = 1; i <= numColumns; i++) {
					   Object o = rset.getObject(i);
					   rowString += o.toString() + "\t";
				   }
				   rowString += "\n";
			   }
//				   System.out.println("numcolumns is " + numColumns);
//				   System.out.println("rowString  is  \n" + rowString);
		       }
			   textQueryArea.setText(colName + "\n" + rowString);
		   } catch(SQLException e){
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	   }
   }
    
   public static void main(String[] args)
	{  
	   JFrame frame = new JDBCInterface();
	   frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   frame.setVisible(true);      
	}
}
