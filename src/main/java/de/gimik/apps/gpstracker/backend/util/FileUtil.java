package de.gimik.apps.gpstracker.backend.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.sun.jersey.core.header.FormDataContentDisposition;

import de.gimik.apps.gpstracker.backend.BackendException;
import de.gimik.apps.gpstracker.backend.web.viewmodel.FileUploadInfo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class FileUtil {
	public static JasperPrint getJasperPrint(String jasperFile, List<?> list, Map<String, Object> parameters) {
		JasperPrint jasperPrint = null;
		try {
			File file = new File(jasperFile);
			String reportPath = file.getParent();
			String fileName = FilenameUtils.getBaseName(file.getAbsolutePath());
			File compiledJasperFile = new File(reportPath, fileName + ".jasper");
			JasperReport jasperReport = null;
			if (compiledJasperFile.exists()) {
				jasperReport = (JasperReport) JRLoader.loadObject(compiledJasperFile);
			} else {
				jasperReport = compileReport(reportPath, fileName);
			}

			if (list == null || list.size() == 0) {
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			} else {
				JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(list);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanDataSource);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return jasperPrint;
	}

	public static JasperReport compileReport(final String reportsPath, String reportName) throws Throwable {
		JasperDesign jasperDesign = JRXmlLoader.load(new File(reportsPath, reportName + ".jrxml"));
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		JRSaver.saveObject(jasperReport, new File(reportsPath, reportName + ".jasper"));
		// toLog("Saving compiled report to: " + reportsPath + reportName +
		// ".jasper");
		// Compile sub reports
		JRElementsVisitor.visitReport(jasperReport, new JRVisitor() {
			@Override
			public void visitBreak(JRBreak breakElement) {
			}

			@Override
			public void visitChart(JRChart chart) {
			}

			@Override
			public void visitCrosstab(JRCrosstab crosstab) {
			}

			@Override
			public void visitElementGroup(JRElementGroup elementGroup) {
			}

			@Override
			public void visitEllipse(JREllipse ellipse) {
			}

			@Override
			public void visitFrame(JRFrame frame) {
			}

			@Override
			public void visitImage(JRImage image) {
			}

			@Override
			public void visitLine(JRLine line) {
			}

			@Override
			public void visitRectangle(JRRectangle rectangle) {
			}

			@Override
			public void visitStaticText(JRStaticText staticText) {
			}

			@Override
			public void visitSubreport(JRSubreport subreport) {
				try {
					String expression = subreport.getExpression().getText().replace(".jasper", "");
					StringTokenizer st = new StringTokenizer(expression, "\"/");
					String subReportName = null;
					while (st.hasMoreTokens())
						subReportName = st.nextToken();
					// Sometimes the same subreport can be used multiple times,
					// but
					// there is no need to compile multiple times
					// if (completedSubReports.contains(subReportName))
					// return;
					// completedSubReports.add(subReportName);
					compileReport(reportsPath, subReportName);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			@Override
			public void visitTextField(JRTextField textField) {
			}

			@Override
			public void visitComponentElement(JRComponentElement componentElement) {
			}

			@Override
			public void visitGenericElement(JRGenericElement element) {
			}
		});
		return jasperReport;
	}

	public static final String appendFilename(String originalName, String preceeding) {
		return FilenameUtils.getBaseName(originalName) + "_" + preceeding + "."
				+ FilenameUtils.getExtension(originalName);
	}

	public static final String setFilename(String originalName, String newName) {
		if (originalName == null)
			return newName + ".png";
		String extension = FilenameUtils.getExtension(originalName) == null ? "png"
				: FilenameUtils.getExtension(originalName);
		return newName + "." + extension;
	}

	public static void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation, long maxBytes)
			throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int read = 0;
		byte[] bytes = new byte[1024];
		int byteCount = 0;

		while ((read = uploadedInputStream.read(bytes)) != -1) {
			byteCount += read;
			if (byteCount > maxBytes)
				break;
			out.write(bytes, 0, read);
		}
		out.flush();

		if (byteCount <= maxBytes) {
			File uploadedFile = new File(uploadedFileLocation);
			OutputStream fileOut = new FileOutputStream(uploadedFile);
			out.writeTo(fileOut);

			fileOut.flush();
			fileOut.close();
		}
		out.close();

		if (byteCount > maxBytes) {

			throw new MaxUploadSizeExceededException(maxBytes);
		}

	}
	public static FileUploadInfo uploadBasicFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
			String directoryFileUpload, String folderName) {
		
		FileUploadInfo fileUploadInfo = new FileUploadInfo();
		String fileName = UUID.randomUUID().toString();

		String uploadFileName = setFilename(fileDetail.getFileName(), fileName);
//		String extension = FilenameUtils.getExtension(uploadFileName);
//		InputStream resizeImage = resizeImage(uploadedInputStream, extension);
		try {
			File uploadFolder = new File(directoryFileUpload, folderName);
			File uploadFile = new File(uploadFolder, uploadFileName);
			FileUtils.forceMkdir(uploadFolder);
			FileUtil.writeToFile(uploadedInputStream, uploadFile.getAbsolutePath(),
					Constants.IMAGE_UPLOAD_FILE_MAX_SIZE);
		} catch (Exception e) {
			throw new BackendException(Constants.ErrorCode.IMAGE_UPLOAD_ERROR);
		}
		fileUploadInfo.setFileUrl("/" + folderName + "/" + uploadFileName);

		return fileUploadInfo;
	}
	public static FileUploadInfo uploadFileImage(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
			String directoryFileUpload, String folderName) {
		return uploadFileImage(uploadedInputStream, fileDetail, directoryFileUpload, folderName, "");
	}

	public static FileUploadInfo uploadFileImage(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
			String directoryFileUpload, String folderName, String oldFileName) {
		
		FileUploadInfo fileUploadInfo = new FileUploadInfo();
		String fileName = UUID.randomUUID().toString();
		if (!StringUtils.isEmpty(oldFileName))
			fileName = oldFileName;
		
		String uploadFileName = setFilename(fileDetail.getFileName(), fileName);
		String extension = FilenameUtils.getExtension(uploadFileName);
		InputStream resizeImage = resizeImage(uploadedInputStream, extension);
		try {
			File uploadFolder = new File(directoryFileUpload, folderName);
			File uploadFile = new File(uploadFolder, uploadFileName);
			FileUtils.forceMkdir(uploadFolder);
			FileUtil.writeToFile(resizeImage, uploadFile.getAbsolutePath(),
					Constants.IMAGE_UPLOAD_FILE_MAX_SIZE);
		} catch (Exception e) {
			throw new BackendException(Constants.ErrorCode.IMAGE_UPLOAD_ERROR);
		}
		fileUploadInfo.setFileUrl("/" + folderName + "/" + uploadFileName);

		return fileUploadInfo;
	}
	public static FileUploadInfo uploadFileVideo(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
			String directoryFileUpload, String folderName) {
		
		FileUploadInfo fileUploadInfo = new FileUploadInfo();
		String fileName = UUID.randomUUID().toString();

		String uploadFileName = setFilename(fileDetail.getFileName(), fileName);
//		String extension = FilenameUtils.getExtension(uploadFileName);
//		InputStream resizeImage = resizeImage(uploadedInputStream, extension);
		try {
			File uploadFolder = new File(directoryFileUpload, folderName);
			File uploadFile = new File(uploadFolder, uploadFileName);
			FileUtils.forceMkdir(uploadFolder);
			FileUtil.writeToFile(uploadedInputStream, uploadFile.getAbsolutePath(),
					Constants.IMAGE_UPLOAD_FILE_MAX_SIZE);
		} catch (Exception e) {
			throw new BackendException(Constants.ErrorCode.IMAGE_UPLOAD_ERROR);
		}
		fileUploadInfo.setFileUrl("/" + folderName + "/" + uploadFileName);

		return fileUploadInfo;
	}

	public static void deleteFile(String fileDirecttody) {
		try {
			File file = new File(fileDirecttody);
			FileUtils.deleteQuietly(file);
		} catch (Exception e) {

		}
	}

	public static InputStream resizeImage(InputStream file, String fileType) {
		Integer max_size = 1200;
		try {
			BufferedImage originalImage = ImageIO.read(file);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			if (width > height) {
				if (width > max_size) {
					height = (int) (height * (new Double(max_size) / new Double(width)));
					width = max_size;
				}
			} else {
				if (height > max_size) {
					width = (int) (width * (new Double(max_size) / new Double(height)));
					height = max_size;
				}
			}
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizedImage = new BufferedImage(width, height, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, width, height, null);
			g.dispose();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, fileType, os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return is;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String createQrcodeFile(String fileDiectory, String folder, String myCodeText) {
		int size = 300;
		String fileType = "png";
		String fileName = UUID.randomUUID() + "." + fileType;
		String url = "/" + folder + "/" + fileName;
		try {
			FileUtils.forceMkdir(new File(fileDiectory + "/" + folder));
			File myFile = new File(fileDiectory + "/" + url);
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
//            int CrunchifyWidth = byteMatrix.getWidth();
			BufferedImage image = MatrixToImageWriter.toBufferedImage(byteMatrix);
//            String fileLogo =  "";
//            if(isKegggyInfo)
//                fileLogo = "kegggyInfo.png";
//            else
//                fileLogo = "kegggy.png";
//            BufferedImage overlay = ImageIO.read(new File(remoteClientInfo.getWebRealPath()+"images//", fileLogo));
//          //Calculate the delta height and width
//            int deltaHeight = image.getHeight() - overlay.getHeight();
//            int deltaWidth  = image.getWidth()  - overlay.getWidth();

//            image = MatrixToImageWriter.toBufferedImage(matrix);
//            image.createGraphics();
//            image.
			BufferedImage combined = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) combined.getGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//            graphics.drawImage(overlay, (int)Math.round(deltaWidth/2), (int)Math.round(deltaHeight/2), null);

//            graphics.setColor(Color.WHITE);
////            graphics.set
//            graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
//            graphics.setColor(Color.BLACK);
//
//            for (int i = 0; i < CrunchifyWidth; i++) {
//                for (int j = 0; j < CrunchifyWidth; j++) {
//                    if (byteMatrix.get(i, j)) {
//                        graphics.fillRect(i, j, 1, 1);
//                    }
//                }
//            }
			ImageIO.write(combined, fileType, myFile);
		} catch (WriterException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return url;
	}
	public static String createBarcodeFile(String fileDiectory, String folder, String myCodeText) {
		try {
			String fileType = "png";
			String fileName = UUID.randomUUID() + "." + fileType;
			String url = "/" + folder + "/" + fileName;
			FileUtils.forceMkdir(new File(fileDiectory + "/" + folder));
			File myFile = new File(fileDiectory + "/" + url);
			int width = 500;
			int height = 200; // change the height and width as per your requirement

			// (ImageIO.getWriterFormatNames() returns a list of supported formats)
			String imageFormat = "png"; // could be "gif", "tiff", "jpeg" 

			BitMatrix bitMatrix = new Code128Writer().encode(myCodeText, BarcodeFormat.CODE_128, width, height);
			
			MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, new FileOutputStream(myFile));
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return "";
	}
	public static String copyFile(String path,Integer id,String folder,String direc ) {
		if(!StringUtils.isEmpty(path)){
			File sourceFile = new File(direc + path);
			String fileName = sourceFile.getName();
			String destName = "copy_"+id+"_"+new Date().getTime()+ fileName;
			String destUrl = folder+"/"+destName;
			File destFile = new File(direc + destUrl);
			try {
				FileUtils.forceMkdir(new File(direc + folder));
				FileCopyUtils.copy(sourceFile, destFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			return destUrl;
		}
		return "";
	}
	
	 public static boolean deleteDirectory(File dir) {
	        if (dir.isDirectory()) {
	            File[] children = dir.listFiles();
	            for (int i = 0; i < children.length; i++) {
	                boolean success = deleteDirectory(children[i]);
	                if (!success) {
	                    return false;
	                }
	            }
	        }

	        // either file or an empty directory
//	        System.out.println("removing file or directory : " + dir.getName());
	        return dir.delete();
	    }

	    /*
	     * Incorrect way to delete a directory in Java
	     */
	    public static void deleteDirectory(String file) {
	        File directory = new File(file);
	        File[] children = directory.listFiles();
//	        for (File child : children) {
//	            System.out.println(child.getAbsolutePath());
//	        }

	        // let's delete this directory
	        // it will not work because directory has sub-directory
	        // which has files inside it.
	        // In order to delete a directory,
	        // you need to first delete its files or contents.
	        boolean result = directory.delete();
//	        if (result) {
//	            System.out.printf("Directory '%s' is successfully deleted",
//	                                directory.getAbsolutePath());
//	        } else {
//	            System.out.printf("Failed to delete directory '%s' %n",
//	                                directory.getAbsolutePath());
//	        }
	    }
	
//    public static void main(String[] args) throws IOException {
//    	MappingLanguageInfo languageInfo = new MappingLanguageInfo();
//    	languageInfo.setBuilding("Building");
//    	languageInfo.setFloor("Floor");
//    	languageInfo.setOriginalState("Original State");
//    	languageInfo.setProjectNumber("Project Number");
//    	languageInfo.setProtectiveClass("Protective Class");
//    	languageInfo.setRoomInformation("Room Information");
//    	languageInfo.setRoomNumber("Room Number");
//    	languageInfo.setSpatialUse("Spatial Use");
//    	Room room= new Room();
//    	room.setId(1);
//    	room.setBuilding("Building");
//    	room.setFloor("Floor");
//    	room.setOriginalState(true);
//    	Project project = new Project();
//    	project.setProjectNumber("Project Number");
//    	room.setProject(project);
//    	room.setProtectiveClass("Protective Class");
//    	room.setRoomInformation("Room Information");
//    	room.setRoomNumber("Room Number");
//    	room.setSpatialUse("Spatial Use");
//    	FileUploadInfo fileUploadInfo = FileUtil.createQrcodeForRoom(languageInfo, room);
//	}
	public static void main(String[] args) {
//		FileCopyUtils.copy(in, out);C:\Temp\allpresan\
		FileUtil.deleteDirectory(new File("C:/Temp/allpresan/tesstcopy doan"));
	}

}
