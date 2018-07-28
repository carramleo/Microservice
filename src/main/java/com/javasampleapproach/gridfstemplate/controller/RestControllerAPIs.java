package com.javasampleapproach.gridfstemplate.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
//funciones del microservicio
@RestController
@RequestMapping("/api")
public class RestControllerAPIs {

	@Autowired
	GridFsOperations gridOperations;
	
	// esta variable se usa para almacenar xmlId para otras acciones como: findOne o delete
	private String xmlFileId = "";

	@GetMapping("/save")
	public String saveFiles() throws FileNotFoundException {
		// Define metaData
		DBObject metaData = new BasicDBObject();
		metaData.put("organization", "JavaSampleApproach");
		
		/**
		 * 1. save an xml file to MongoDB
		 */
		
		// Obtener archivo de entrada
		InputStream xmlStream = new FileInputStream("C:\\Users\\carlo\\Desktop\\Ingeniería de Telecomunicaciones\\carlos2.xml");
		
		metaData.put("type", "xml");
		metaData.put("user", "carramleo");
		
		// Almacenar fichero en MongoDB
		xmlFileId = gridOperations.store(xmlStream, "descargado.xml", "xml/xml", metaData).getId().toString();
		System.out.println("xmlFileId = " + xmlFileId);


		



		return "Done";
	}
	
	@GetMapping("/retrieve/xmlfile")
	public String retrievexmlFile() throws IOException{
		// Leer fichero de MongoDB
		GridFSDBFile xmlFile = gridOperations.findOne(new Query(Criteria.where("_id").is(xmlFileId)));
		
		// Save file back to local disk
		xmlFile.writeTo("C:\\Users\\carlo\\Desktop\\Ingeniería de Telecomunicaciones\\descargado.xml");
		
		System.out.println("xml File Name:" + xmlFile.getFilename());
		
		return "Done";
	}
	

	
	@GetMapping("/delete/xml")
	public String deleteFile(){
		// borrar xml via id
		gridOperations.delete(new Query(Criteria.where("_id").is(xmlFileId)));
		
		return "Done";
	}
}
