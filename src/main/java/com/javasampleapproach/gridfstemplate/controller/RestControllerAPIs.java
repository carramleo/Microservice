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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.net.UnknownHostException;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//funciones del microservicio

@RestController
@RequestMapping("/api")
public class RestControllerAPIs {

    @Autowired
    GridFsOperations gridOperations;

    // esta variable se usa para almacenar xmlId para otras acciones como: findOne o delete
    private String xmlFileId = "";

    /*
	@GetMapping("/save")
	public String saveFiles(InputStream fichero) throws FileNotFoundException {
		// Define metaData
		DBObject metaData = new BasicDBObject();
		metaData.put("organization", "JavaSampleApproach");
		
		/**
		 * 1. save an xml file to MongoDB
     */
    // Obtener archivo de entrada
    //InputStream xmlStream = new FileInputStream(fichero.toString());
    /*	
		metaData.put("type", "xml");
		metaData.put("user", "carramleo");
		
		// Almacenar fichero en MongoDB
		xmlFileId = gridOperations.store(fichero, fichero.toString(), "application/xml", metaData).getId().toString();
		System.out.println("xmlFileId = " + xmlFileId);


		return "Guardado en base de datos";
	}
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/xml")
    public Response uploadFile(@Context HttpServletRequest req) throws UnknownHostException, Exception {

        MongoClient mongoClient = new MongoClient("localhost", 27017);

        DB mongoDB = mongoClient.getDB("GAM-mongodb");

        if (ServletFileUpload.isMultipartContent(req)) {
            FileItemFactory fiFactory = new DiskFileItemFactory();
            ServletFileUpload fileUpload = new ServletFileUpload(fiFactory);

             List<FileItem> listItems = fileUpload.parseRequest((RequestContext) req);
            Iterator<FileItem> iter = listItems.iterator();

            GridFS fileStore = new GridFS(mongoDB, "filestore");
            while (iter.hasNext()) {
                FileItem item = iter.next();

                if (!item.isFormField()) {
                    InputStream in = item.getInputStream();



                    GridFSInputFile inputFile = fileStore.createFile(in);
                    inputFile.setId(item.getName());
                    inputFile.setFilename(item.getName());
                    inputFile.save();
                    in.close();
                }
            }
        }

        String status = "Upload has been successful";

        return Response.status(200).entity(status).build();
    }

    @GetMapping("/retrieve/xmlfile")
    public String retrievexmlFile() throws IOException {
        // Leer fichero de MongoDB
        GridFSDBFile xmlFile = gridOperations.findOne(new Query(Criteria.where("_id").is(xmlFileId)));

        // Save file back to local disk
        xmlFile.writeTo("C:\\Users\\carlo\\Desktop\\Ingeniería de Telecomunicaciones\\descargado.xml");

        System.out.println("xml File Name:" + xmlFile.getFilename());

        return "Done";
    }

    @GetMapping("/delete/xml")
    public String deleteFile() {
        // borrar xml via id
        gridOperations.delete(new Query(Criteria.where("_id").is(xmlFileId)));

        return "Done";
    }
}
