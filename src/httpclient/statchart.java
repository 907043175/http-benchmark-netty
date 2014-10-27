package httpclient;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.mail.util.ByteArrayDataSource;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;

public class statchart {
	public statchart() {
    }
	
    //������ݼ�
    public XYDataset createdataset() {
    	TimeSeries linedataset = new TimeSeries("QPS DISTRIBUTION CURVE");
    	Iterator<Map.Entry<String, String>> iter = httpbenchmark.qps_stat.entrySet().iterator();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	int flag = 0;//��һ���㶪��
    	try {
    		while (iter.hasNext()) { 
    			Map.Entry<String, String> entry = iter.next();
    			if (flag == 1) {
    				linedataset.addOrUpdate(new Second(sdf.parse(entry.getKey())), Double.parseDouble(entry.getValue()));
    			}
    			flag = 1;
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	TimeSeriesCollection timeSeriesCollection =new TimeSeriesCollection(linedataset);
		return timeSeriesCollection;
    }

    //����ͼ�����JFreeChart
	/*
	*������Ŀ������JFreeChart
	*������������� Plot �䳣�������У�CategoryPlot, MultiplePiePlot, PiePlot , XYPlot 
	*/
    public void createChart() {
    	try {
    		/*//����������ʽ
    		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
    		//���ñ�������
    		standardChartTheme.setExtraLargeFont(new Font("����", Font.BOLD, 20));
    		//����ͼ��������
    		standardChartTheme.setRegularFont(new Font("����", Font.PLAIN, 15));
    		//�������������
    		standardChartTheme.setLargeFont(new Font("����", Font.PLAIN, 15));
    		//Ӧ��������ʽ
    		ChartFactory.setChartTheme(standardChartTheme);*/
    		
    		//����ͼ�����
    		JFreeChart chart = ChartFactory.createTimeSeriesChart(
    				"QPS Distribution Curve",// ������Ŀ���ַ�������
    				"Time", // ����
    				"QPS", // ����
    				this.createdataset(), // ������ݼ�
    				false, // ��ʾͼ��
    				false, // �������ɹ���
    				false // ��������URL��ַ
    		);
    		//������Ŀ������chart  ��������chart�ı�����ɫ
    		chart.setBackgroundPaint(Color.LIGHT_GRAY);
    		// ����ͼ��
    		XYPlot plot = chart.getXYPlot();
    		// ͼ�����Բ���
    		plot.setBackgroundPaint(Color.white);
    		plot.setDomainGridlinesVisible(true);  //���ñ����������Ƿ�ɼ�
    		plot.setDomainGridlinePaint(Color.BLACK); //���ñ�����������ɫ
    		plot.setRangeGridlinePaint(Color.GRAY);
    		plot.setNoDataMessage("Data Not Available");//û������ʱ��ʾ������˵��
		  
    		//X��
    		DateAxis dateaxis = (DateAxis) plot.getDomainAxis();
    		dateaxis.setAutoTickUnitSelection(true);
    		dateaxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            //Y��
    		NumberAxis numberaxis = (NumberAxis)plot.getRangeAxis();
    		numberaxis.setAutoTickUnitSelection(true);
                  
			// ������Ⱦ����,��Ҫ�Ƕ�����������
    		XYItemRenderer renderer = (XYItemRenderer )plot.getRenderer();
			renderer.setSeriesPaint(0, Color.blue);    //�������ߵ���ɫ
			renderer.setBaseItemLabelsVisible(false);
			renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
			                ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_LEFT));
			renderer.setBaseItemLabelFont(new Font("Dialog", Font.PLAIN, 12));  //������ʾ�۵�������״
			plot.setRenderer(renderer);
			
			XYLineAndShapeRenderer r = (XYLineAndShapeRenderer)plot.getRenderer();
			r.setBaseShapesVisible(true);
			r.setBaseShapesFilled(true);
			r.setBaseItemLabelsVisible(false);
			
			// �����ļ������
			/*FileOutputStream fos_jpg = new FileOutputStream("./bloodSugarChart.png");
			ChartUtilities.writeChartAsJPEG(fos_jpg, chart, 720, 480);
			fos_jpg.close();*/
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsJPEG(bos, chart, 720, 480);
			httpbenchmark.datasource = new ByteArrayDataSource(bos.toByteArray(), "image/jpeg");
			bos.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }

    public static void main(String[] args) {
    	for (int i = 0; i < 20; i++) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ct = sdf.format(date);
			//System.err.println(ct);
			
			//DecimalFormat df=new DecimalFormat("0.00");
			//double elapsed = (System.currentTimeMillis() - easysocketbenchmark.start_time)/1000.0;
			//String qps = df.format((easysocketbenchmark.total_res/(elapsed)));
			httpbenchmark.qps_stat.put(ct, String.valueOf(1000+i));
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
    	}
    	new statchart().createChart();
    }
}
