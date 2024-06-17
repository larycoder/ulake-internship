package org.usth.ict.ulake.table.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.compress.utils.CountingInputStream;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.table.model.Table;
import org.usth.ict.ulake.table.model.TableColumnModel;
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;
import org.usth.ict.ulake.table.parser.Csv;
import org.usth.ict.ulake.table.parser.Parser;
import org.usth.ict.ulake.table.parser.Xlsx;
import org.usth.ict.ulake.table.persistence.TableCellRepository;
import org.usth.ict.ulake.table.persistence.TableColumnRepository;
import org.usth.ict.ulake.table.persistence.TableRepository;
import org.usth.ict.ulake.table.persistence.TableRowRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;


@Path("/table")
@Produces(MediaType.APPLICATION_JSON)
public class TableResource {
    private static final Logger log = LoggerFactory.getLogger(TableResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    LakeHttpResponse response;

    @Inject
    TableRepository repo;

    @Inject
    TableRowRepository repoRow;

    @Inject
    TableColumnRepository repoColumn;

    @Inject
    TableCellRepository repoCell;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    DashboardService dashboardService;

    @Inject
    @RestClient
    CoreService coreService;

    @GET
    @Operation(summary = "List all tables")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("Admin")) {
            return response.build(200, "", repo.listAll());
        }
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        return response.build(200, "", repo.list("ownerId", userId));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one table info")
    public Response one(@PathParam("id") @Parameter(description = "User id to search") Long id) {
        TableModel table = repo.findById(id);
        return response.build(200, null, table);
    }


    @GET
    @Path("/{id}/columns")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get table columns")
    public Response column(@PathParam("id") @Parameter(description = "Table id to search") Long id) {
        HashMap<String, String> colInfo = new HashMap<>();
        var cols = repoColumn.find("table.id", id);
        for (var col: cols.list()) {
            colInfo.put(col.columnName, col.dataType);
        }
        return response.build(200, null, colInfo);
    }

    @PUT
    @Path("/columns")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update table columns")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveColumn(@RequestBody(description = "Columns to be updated") List<TableColumnModel> cols) {
        int updated = 0;
        for (var col: cols) {
            TableColumnModel entity = repoColumn.findById(col.id);
            if (entity == null) {
                continue;   // ignore
            }
            if (!Utils.isEmpty(col.columnName)) entity.columnName = col.columnName;
            if (!Utils.isEmpty(col.dataType)) entity.dataType = col.dataType;
            if (col.groupBy != null) entity.groupBy = col.groupBy;
            log.info("data type of id {} will be {}. entity dataType is {}", col.id, col.dataType, entity.dataType);
            repoColumn.persist(entity);
            updated++;
        }
        return response.build(200, null, updated);
    }

    @GET
    @Path("/{id}/data")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one table data")
    public Response data(@PathParam("id") @Parameter(description = "User id to search") Long id) {
        HashMap<String, Object> ret = new HashMap<>();

        // table info
        TableModel table = repo.findById(id);
        ret.put("id", table.id);
        ret.put("name", table.name);
        ret.put("ownerId", table.ownerId);

        // columns
        var cols = repoColumn.list("table.id", id);
        ret.put("columns", cols);

        // cells
        var cells = repoCell.find("table.id", id);
        var rows = new HashMap<Long, ArrayList<String>>();
        for (var cell: cells.list()) {
            if (rows.get(cell.row.id) == null) {
                rows.put(cell.row.id, new ArrayList<String>());
            }
            rows.get(cell.row.id).add(cell.value);
        }
        ret.put("rows", rows);

        return response.build(200, null, ret);
    }

    @POST
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Make a new table")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@RequestBody(description = "Multipart form data. metadata: extra json info " +
                            "{name:'table name', format: 'csv/xls'}). file: csv/xls data to save")
                        MultipartFormDataInput input) throws IOException {
        TableMetadata meta = null;
        InputStream is = null;

        // iterate through form data to extract metadata and file
        Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
        for (var formData : formDataMap.entrySet()) {
            //log.info("POSTzzz: {} {}", formData.getKey(), formData.getValue().get(0).getBodyAsString());
            if (formData.getKey().equals("metadata")) {
                try {
                    String metaJson = formData.getValue().get(0).getBodyAsString();
                    meta = mapper.readValue(metaJson, TableMetadata.class);
                } catch (JsonProcessingException e) {
                    log.error("error parsing metadata json {}", e.getMessage());
                }
            } else if (formData.getKey().equals("file")) {
                is = formData.getValue().get(0).getBody(InputStream.class, null);
            }
        }

        if (meta == null || is == null) {
            return response.build(403);
        }

        log.info("Input stream mark supported {}", is.markSupported());
        if (is.markSupported()) {
            log.info("Marking place for later reset()");
            is.mark(100*1048576);
        }
        CountingInputStream cis = new CountingInputStream(is);

        // save a new table meta info
        TableModel table = new TableModel();

        Long now = new Date().getTime()/1000;
        table.name = meta.name;
        table.format = meta.format;
        table.creationTime = now.longValue();
        table.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        repo.persist(table);

        // parse input stream
        Parser parser = null;
        Table tableData = null;
        log.info("Parsing uploaded format {}", meta.format);
        if (meta.format.equals("csv")) {
            parser = new Csv();
        }
        else if (meta.format.equals("xlsx")) {
            parser = new Xlsx();
        }

        if (parser != null) {
            tableData = parser.parse(repo, repoRow, repoColumn, repoCell, cis, table, meta);
        }

        if (tableData != null) {
            // push to core backend
            is.reset();
            String bearer = "bearer " + jwt.getRawToken();
            FileFormModel model = new FileFormModel();
            model.fileInfo = new FileModel();
            model.fileInfo.name = meta.name + "." + meta.format;
            model.fileInfo.size = cis.getBytesRead();
            model.is = cis;
            log.info("Pushing to core {}, format {}, size {}", meta.name, meta.format, model.fileInfo.size);

            dashboardService.newFile(bearer, model);
            return response.build(200, null, tableData);
        }
        else {
            return response.build(415, "Only CSV/XLSX files are supported");
        }
    }

    private String getExtension(String fileName) {
        if (fileName.indexOf(".") > 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
            return fileName;
    }

    private String removeExtension(String fileName) {
        if (fileName.contains(".")) return fileName.substring(0, fileName.lastIndexOf('.'));
        return fileName;
    }

    @POST
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Convert an existing CSV/XLS file to a table")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/convert")
    public Response convert(@HeaderParam("Authorization") String bearer, @PathParam("id") @Parameter(description = "File id to convert") Long id) throws IOException {
        FileModel fileModel = dashboardService.fileInfo(id, bearer).getResp();
        InputStream is = coreService.objectDataByFileId(id, bearer);
        CountingInputStream cis = new CountingInputStream(is);

        // save a new table meta info
        TableMetadata meta = new TableMetadata();
        meta.name = removeExtension(fileModel.name);
        meta.format = getExtension(fileModel.name);

        TableModel table = new TableModel();
        Long now = new Date().getTime();
        table.name = meta.name;
        table.format = meta.format;
        table.creationTime = now.longValue();
        table.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        repo.persist(table);

        // parse input stream
        Parser parser = null;
        Table tableData = null;
        log.info("Parsing uploaded format {}", meta.format);
        if (meta.format.equals("csv")) {
            parser = new Csv();
        }
        else if (meta.format.equals("xlsx")) {
            parser = new Xlsx();
        }

        if (parser != null) {
            tableData = parser.parse(repo, repoRow, repoColumn, repoCell, cis, table, meta);
        }

        if (tableData != null) {
            return response.build(200, null, tableData);
        }
        else {
            return response.build(415, "Only CSV/XLSX files are supported");
        }
    }

    @DELETE
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete an existing table")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@HeaderParam("Authorization") String bearer, @PathParam("id") @Parameter(description = "File id to delete") Long id) throws IOException {
        long cellDeleted = repoCell.delete("table.id", id);
        long rowDeleted = repoRow.delete("table.id", id);
        long columnDeleted = repoColumn.delete("table.id", id);
        boolean tableDeleted = repo.deleteById(id);
        return response.build(200, null, cellDeleted + rowDeleted + columnDeleted + (tableDeleted ? 1 : 0));
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about tabular datas")
    @RolesAllowed({ "User", "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        // get requests from other service
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("tables", repo.count());
        ret.put("rows", repoRow.count());
        ret.put("columns", repoColumn.count());
        ret.put("cells", repoCell.count());
        return response.build(200, "", ret);
    }
}
