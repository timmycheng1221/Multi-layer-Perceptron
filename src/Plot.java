import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Plot extends Canvas{
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean data, train, test, transformation;
	private ArrayList<Double> x =  new ArrayList<Double>();
	private ArrayList<Double> y =  new ArrayList<Double>();
	private ArrayList<Integer> group = new ArrayList<Integer>();
	private Color[] groupColor = {Color.red, Color.orange, Color.green, Color.blue, Color.cyan, Color.magenta};
	
	public Plot(int x, int y) {
		setBounds(x, y, 400, 400);
		data = false;
		train = false;
		test = false;
		transformation = false;
	}
	
	public void paint(Graphics g) {
		//draw x and y axis
		Graphics2D  g2d = (Graphics2D) g; 
		g.translate(200, 200);
		g.drawLine(200, 0, -200, 0);
		g.drawLine(0, 200, 0, -200);
		
		//draw train or test data
		if(data && (test || train)) {
			for(int i = 0 ; i < x.size() ; i++) {
				g.setColor(groupColor[group.get(i)]);
				Rectangle2D.Double rect = new Rectangle2D.Double(x.get(i) * 20 - 1, -(y.get(i) * 20 - 1), 2, 2);
				g2d.fill(rect);
			}
		}
		//draw transformation data
		if(data && transformation) {
            for(int i = 0 ; i < x.size() ; i++) {
                g.setColor(groupColor[group.get(i)]);
                Rectangle2D.Double rect = new Rectangle2D.Double(x.get(i) * 200 - 100 - 1, -(y.get(i) * 200 - 100 - 1), 2, 2);
                g2d.fill(rect);
            }
        }
	}
	
	public void cleanData() {
		this.x.clear();
		this.y.clear();
		this.group.clear();
	}
	
	public void readData(double x, double y, int group) {
		this.x.add(x);
		this.y.add(y);
		this.group.add(group);
		data = true;
	}
	
	public boolean getTrain() {
		return train;
	}
	
	public boolean getTest() {
		return test;
	}
	
	public void trainPlot() {
		test = false;
		transformation = false;
		train = true;
		this.repaint();
	}
	
	public void testPlot() {
		train = false;
		transformation = false;
		test = true;
		this.repaint();
	}
	
	public void transformationPlot() {
	    train = false;
        test = false;
        transformation = true;
        this.repaint();
	}
	
	public void blankPlot() {
	    train = false;
        test = false;
        transformation = false;
        this.repaint();
	}
}
