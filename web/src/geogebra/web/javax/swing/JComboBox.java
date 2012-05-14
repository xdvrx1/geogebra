package geogebra.web.javax.swing;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.javax.swing.AbstractJComboBox;

public class JComboBox extends geogebra.common.javax.swing.AbstractJComboBox {

	private ListBox impl = null;

	/**
	 * Creates new wrapper Box
	 */
	public JComboBox() {
		this.impl = new ListBox();
	}
	
	@Override
    public void setVisible(boolean b) {
	    impl.setVisible(b);
	    
    }

	@Override
    public Object getItemAt(int i) {
	    return impl.getItemText(i);
    }

	@Override
    public void setFont(Font font) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setForeground(Color objectColor) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setBackground(Color color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setFocusable(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setEditable(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void addItem(String string) {
	    impl.addItem(string);
	    
    }

	@Override
    public void setSelectedIndex(int selectedIndex) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public int getSelectedIndex() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void addActionListener(ActionListener newActionListener) {
	    // TODO Auto-generated method stub
	    
    }

	public static Widget getImpl(AbstractJComboBox comboBox) {
	    if(!(comboBox instanceof JComboBox))
	    	return null;
	    return ((JComboBox)comboBox).impl;
    }

}
