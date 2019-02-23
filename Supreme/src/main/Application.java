package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class Application {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		long startTime = System.nanoTime();

		String website = "http://www.supremenewyork.com/shop/";
		
		String[] itemtype = { "", "new", "jackets", "shirts", "tops_sweaters", "sweatshirts", "pants", "t-shirts", "hats",
				"bags", "accessories", "shoes", "skate" };
		
		String[] sizetype = {"","small", "medium", "large","x-large"};
		
		String[][] user = { { "order_billing_name", "Kyle Kruskamp" }, { "order_email", "kylekrusk@gmail.com" },
				{ "order_tel", "1234567890" }, { "bo", "123 Happy Drive" }, { "order_billing_zip", "19711" },
				{ "nnaerb", "4444555566667777" }, { "credit_card_month", "12" }, { "credit_card_year", "2024" },
				{ "orcer", "222" }, };
		
		
	    JComboBox jcb_item = new JComboBox(itemtype);
	    JComboBox jcb_size = new JComboBox(sizetype);

	    JTextField jtf_key = new JTextField("Supreme");
	    JTextField jtf_col = new JTextField();
	    
	    JButton j_payment = new JButton("Payment Options");
	    j_payment.addActionListener(new ActionListener() { 
	    	  public void actionPerformed(ActionEvent e) { 
	    		    JPanel paypanel = new JPanel(new GridLayout(0, 1));

	    		    JTextField[] ppbtn = new JTextField[user.length];
	    		    
	    		    for(int i = 0; i<user.length; i++) {
		    		    paypanel.add(new JLabel(user[i][0]));
		    		    ppbtn[i] = new JTextField(user[i][1]);
		    		    paypanel.add(ppbtn[i]);	    		    	
	    		    }
	    		    
	    		    int result = JOptionPane.showConfirmDialog(null, paypanel, "Payment Options",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    		    
	    		    if (result == JOptionPane.OK_OPTION) {
	    		    	boolean changed = false;
		    		    for(int i = 0; i<user.length; i++) {
		    		    	if (user[i][1].compareTo(ppbtn[i].getText()) != 0) {
		    		    		changed = true;
		    		    		user[i][1] = ppbtn[i].getText();
		    		    	}
		    		    }
		    		    if (changed)
		    		    	System.out.println("Payment information updated!");
		    		    else
		    		    	System.out.println("No changes were made.");
	    		    } else
	    		        System.out.println("Closed");
	    	  } 
	    } );

	    JPanel panel = new JPanel(new GridLayout(0, 1));
	    
	    JPanel titlepanel = new JPanel();
	    JLabel title = new JLabel("SupremeBot");
	    title.setFont(new Font("Arial",3,30));
	    title.setForeground(Color.white);
	    titlepanel.add(title);
	    titlepanel.setBackground(Color.red);
	    
	    JPanel authorpanel = new JPanel();
	    JLabel author = new JLabel("by: Kyle Kruskamp & Vincent Mark");
	    author.setForeground(Color.gray);
	    authorpanel.add(author);
	    
	    panel.add(titlepanel);
	    panel.add(authorpanel);
	    panel.add(new JLabel("Category:"));
	    panel.add(jcb_item);
	    panel.add(new JLabel("Keyword:"));
	    panel.add(jtf_key);
	    panel.add(new JLabel("Colour:"));
	    panel.add(jtf_col);
	    panel.add(new JLabel("Size:"));
	    panel.add(jcb_size);
	    panel.add(new JLabel("Edit Payment Options:"));
	    panel.add(j_payment);
	    
	    int result = JOptionPane.showConfirmDialog(null, panel, "SUPREME",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    
	    if (result == JOptionPane.OK_OPTION) {
	        System.out.println("[" + jcb_item.getSelectedItem()
	            + "," + jtf_key.getText()
	            + "," + jtf_col.getText()
	            + "," + jcb_size.getSelectedItem()
	            + "]");
	    } else {
	        System.out.println("Cancelled");
	        System.exit(0);
	    }
		
		String keyword = jtf_key.getText();
		String color = jtf_col.getText();
		int n = jcb_item.getSelectedIndex();
		int s = jcb_size.getSelectedIndex();

		//WebDriver google = Chrome.getDriver();
		WebDriver driver = Chrome.getDriver();
		//driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);

		// Wait until 11:00:00 AM
		System.out.println("waiting...");
		while (!LocalTime.now().isAfter(LocalTime.parse("11:00"))) {
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		}
		System.out.println("Done");
		
		// Wait for Supreme update
		driver.get(website + "new/");
		String orig = driver.findElement(By.xpath("//*[@id=\"container\"]/article[1]/div/a")).getAttribute("href");
		do {
			System.out.println("searching");
			driver.get(website + "all/" + itemtype[n]);
			//driver.navigate().refresh();
		} while (orig.compareTo(driver.findElement(By.xpath("//*[@id=\"container\"]/article[1]/div/a")).getAttribute("href"))==0);

		driver.get(website + "all/" + itemtype[n]);

		int count = driver.findElements(By.xpath("//*[@class=\"inner-article\"]")).size();
		String buffer;

		int i = 1;
		for (i = 1; i <= count; i++) {
			try {
				buffer = driver.findElement(By.xpath("//*[@id=\"container\"]/article[" + i + "]/div/a"))
						.getAttribute("href");
				Document doc = Jsoup.connect(buffer).get();
				buffer = doc.title().substring(9);
				if (buffer.toLowerCase().contains(keyword.toLowerCase())
						&& buffer.toLowerCase().contains(color.toLowerCase())) {
					System.out.println("Article #" + i + ": " + buffer);
					break;

				}
			} catch (IOException e) {
				System.out.println("Could not find item.");
			}
		}

		// Select item from catalog
		WebElement item = driver.findElement(By.xpath("//*[@id=\"container\"]/article[" + i + "]/div/a"));
		item.click();

		// Select size (if applicable)
		if (driver.findElement(By.id("s")).getText() != null) {
			driver.findElement(By.id("s")).sendKeys(sizetype[s].substring(0, 1));
		}
		System.out.println("[" + sizetype[s].substring(0, 1) + "]");
		
		// Adding to cart
		WebElement purchase = driver.findElement(By.xpath("//*[@id=\"add-remove-buttons\"]/input"));
		purchase.click();

		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		
		do {
			driver.get("https://www.supremenewyork.com/checkout");
		} while (!driver.getCurrentUrl().contains("checkout"));

		// Auto fill user information
		WebElement elm;
		for (i = 0; i < user.length; i++) {
			elm = driver.findElement(By.id(user[i][0]));
			elm.click();
			elm.sendKeys(user[i][1]);
		}

		// Check box for terms
		WebElement terms = driver.findElement(By.xpath("//*[@id=\"cart-cc\"]/fieldset/p[2]/label/div/ins"));
		terms.click();

		// Checkout
		driver.findElement(By.name("commit")).click();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.quit();

		// Print total program runtime
		long endTime2 = System.nanoTime();
		long totalTime = (endTime2 - startTime) / 1000000;
		System.out.println(totalTime);
	}	
}

class Chrome {
	public static WebDriver getDriver() {
		File f = new File("src/resources/chromedriver.exe");
		System.setProperty("webdriver.chrome.driver", f.getAbsolutePath());
		return new ChromeDriver();
	}
}