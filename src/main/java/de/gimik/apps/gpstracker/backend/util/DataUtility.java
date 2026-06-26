package de.gimik.apps.gpstracker.backend.util;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public final class DataUtility {
	private static List<String> logActions;
	private static List<String> logObjects;
	
	static{
		initLogActions();
		initLogObjects();
	}	
		
	private static final void initLogActions() {
		logActions = new ArrayList<String>();
		
		logActions.add(Constants.Action.ADD);
		logActions.add(Constants.Action.UPDATE);
		logActions.add(Constants.Action.DELETE);
		logActions.add(Constants.Action.CHANGE_PASSWORD);
//		logActions.add(Constants.Action.SEND_EMAIL);
	}

	private static final void initLogObjects(){
		logObjects = new ArrayList<String>();
		logObjects.add(Constants.Object.PROFILE);
     
	}
		
	public static final Collection<String> getLogActions(){
		return logActions;
	}
	
	public static final Collection<String> getLogObjects(){
		return logObjects;
	}
	
	public static final String formatDisplayDate(Date date){
		if (date != null){
			return new SimpleDateFormat("dd/MM/yyyy").format(date);
		}else{
			return "";
		}
	}
	
	public static final String formatDisplayDateTime(Date date){
		if (date != null){
			return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
		}else{
			return "";
		}
	}	
	
	public static final String formatDisplayDateTimeAtMinute(Date date){
		if (date != null){
			return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
		}else{
			return "";
		}
	}	
	
	public static final Date getMinDate(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			return df.parse("01/01/1900 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final Date getMaxDate(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			return df.parse("31/12/2900 23:59:59");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final Date getFromDate(Date date){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			return df.parse(formatDisplayDate(date) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final Date getToDate(Date date){		
		return getFromDate(addDay(date, 1));
	}
	
	public static final Date parseFeedLastUpdated(String lastUpdatedText){
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy.HH.mm");
		
		try{
			return df.parse(lastUpdatedText);
		}catch (Exception ex){
			df = new SimpleDateFormat("dd.MM.yyyy.H.mm");
			try{
				return df.parse(lastUpdatedText);
			}catch (Exception ex1){
				return null;
			}
		}
	}
	
	private static final Date addDay(Date date, int daysToAdd){
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(Calendar.DATE, daysToAdd);
		
		return cal.getTime();
	}

    /**
     * @deprecated Not used in Rest API Context
     */
    @Deprecated
	public static final String getClientIP(){
		return ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes())
	       							.getRequest().getRemoteAddr();
	}
	
	public static final String formatMonth(int month){
		return StringUtils.leftPad(String.valueOf(month), 2, "0");
	}
	
	public static final String formatNumber(int number){
		NumberFormat format = new DecimalFormat("#,###,###");
		
		return format.format(number);
	}
	
	public static <T> List<T> mergeLists(List<T>... lists) {
		List<T> merged = new ArrayList<T>();
		
		for(List<T> list : lists)
			if (list != null && list.size() > 0)
				merged.addAll(list);
		
		return merged;
	}
	
	public static <T> void addIfNotNull(List<T> list, T... items) {
		for(T item : items)
			if (item != null)
				list.add(item);
	}
	
	public static boolean isEmailValid(String email) {
		return GenericValidator.isEmail(email);
	}
	
//	public static final String buildFileName(Patient patient){
//		return buildFileName(new PatientInfo(patient));
//	}
	
//	public static final String buildFileName(PatientInfo patient){
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("diary_data");
//
//		if (patient != null){
//			sb.append("_").append(patient.getPatientFirstName() == null ? "" : patient.getPatientFirstName());
//			sb.append("_").append(patient.getPatientName() == null ? "" : patient.getPatientName());
//		}
//
//		sb.append(".PDF");
//
//		return sb.toString();
//	}
//
//	public static final String buildChartName(String chartTitle, PatientInfo patient){
//		StringBuilder sb = new StringBuilder();
//
//		sb.append(chartTitle);
//
//		if (patient != null){
//			sb.append(" ").append(patient.getPatientFirstName() == null ? "" : patient.getPatientFirstName());
//			sb.append(" ").append(patient.getPatientName() == null ? "" : patient.getPatientName());
//		}
//
//		return sb.toString();
//	}
	
	public static final void pngToPdf(JFreeChart chart, int width, int height, OutputStream pdfOutputStream){
		Document document = new Document();
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			document.setPageSize(PageSize.A4.rotate()); 
	      PdfWriter writer = PdfWriter.getInstance(document, pdfOutputStream);
	      writer.open();
	      document.open();
	      byteArrayOutputStream = new ByteArrayOutputStream();
	      ChartUtilities.writeChartAsPNG(byteArrayOutputStream, chart, width, height);	      
	      document.add(Image.getInstance(byteArrayOutputStream.toByteArray()));
	      document.close();
	      writer.close();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally{
	    	try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static final byte[] chart2ByteArray(JFreeChart chart, int width, int height){
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(byteArrayOutputStream, chart, width, height);	     
			
			return byteArrayOutputStream.toByteArray();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally{
	    	try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    return null;
	}
	
//	public static void writeChartToPDF(JFreeChart chart, int width, int height, OutputStream outputStream) {
//	    PdfWriter writer = null;
//	 
//	    Document document = new Document();
//	 
//	    try {
//	        writer = PdfWriter.getInstance(document, outputStream);
//	        document.open();
//	        PdfContentByte contentByte = writer.getDirectContent();
//	        PdfTemplate template = contentByte.createTemplate(width, height);
//	        Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
//	        Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width, height);
//	 
//	        chart.draw(graphics2d, rectangle2d);
//	 
//	        graphics2d.dispose();
//	        contentByte.addTemplate(template, 0, 0);
//	 
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	    document.close();
//	}
}
