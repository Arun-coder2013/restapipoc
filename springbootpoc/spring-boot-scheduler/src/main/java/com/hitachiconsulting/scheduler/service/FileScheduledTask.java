package com.hitachiconsulting.scheduler.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileScheduledTask {

	private static final Logger log = LoggerFactory	.getLogger(FileScheduledTask.class);

	private Set<String> fileList = new HashSet<>();
	
	@Value("${source.scheduledJob.enabled:false}")
	private boolean scheduledJobEnabled;
	
	@Value("${source.file.dir}")
	private String sourceFileDir;
	
	@Value("${source.zippedFile.dir}")
	private String zippedFileDir;


	@Scheduled(fixedRate = 30000)  
	public void pullRandomComment() {
		if (!scheduledJobEnabled) {
			return;
		}
		generateFileList(new File(sourceFileDir));
		compressFileInZipFormat(zippedFileDir);
	}

	 public void compressFileInZipFormat(String zipFile){

	        byte[] buffer = new byte[1024];

	        try{

	       	FileOutputStream fos = new FileOutputStream(zipFile);
	       	ZipOutputStream zos = new ZipOutputStream(fos);

	       	for(String file : this.fileList){

	       		ZipEntry ze= new ZipEntry(file);
	           	zos.putNextEntry(ze);
	           	FileInputStream in = new FileInputStream(sourceFileDir + File.separator + file);
	           	int len;
	           	while ((len = in.read(buffer)) > 0) {
	           		zos.write(buffer, 0, len);
	           	}

	           	in.close();
	       	}

	       	zos.closeEntry();
	       	zos.close();

	       	log.info("Zipped all files");
	       }catch(IOException ex){
	         
	    	   log.error("Exception occured"+ex);
	       }
	      }
	    
	    
	    public void generateFileList(File file){

				if(file.isFile()){
					log.info("file added"+file);
					fileList.add(generateZipEntry(file.getAbsoluteFile().toString()));
				}
			
				if(file.isDirectory()){
					
					String[] subNote = file.list();
					
					for(String filename : subNote){
						
						generateFileList(new File(file, filename));
						}
				}
	    }
	    
	    private String generateZipEntry(String file){
	    	
	    	return file.substring(sourceFileDir.length()+1, file.length());
	    }

}
