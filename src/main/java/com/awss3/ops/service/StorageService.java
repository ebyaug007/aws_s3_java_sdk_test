package com.awss3.ops.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {
	@Autowired
	AmazonS3 s3Client;

	@Value("${application.bucket.name}")
	private String bucketName;

	public String uploadFile(MultipartFile file) {
		File fileObj = convertMultipartFiletoFile(file);
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		fileObj.delete();
		return "File Uploaded";
	}

	public byte[] downloadFile(String fileName)
	{
		S3Object s3Object = s3Client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		byte[] content;
		try {
			content = IOUtils.toByteArray(inputStream);
			return content;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String deleteFile(String fileName)
	{
		s3Client.deleteObject(bucketName, fileName);
		return "Removed File";
	}
	
	private File convertMultipartFiletoFile(MultipartFile file) {
		File fileObj = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(fileObj)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return fileObj;
	}
}
