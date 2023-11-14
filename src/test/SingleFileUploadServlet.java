package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Servlet implementation class SingleFileUploadServlet
 */
@WebServlet("/upload_single")
@MultipartConfig(location = "C:/SivaTempFiles/singlefile_upload", fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 10, // 10MB
		maxRequestSize = 1024 * 1024 * 11)
public class SingleFileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String apiUrl;
	private String apiKey;
	private String chatHistory;
	public RequestSpecification request;
	public Response response;
	public String chatGPTError = null;
	
	public SingleFileUploadServlet(){
		super();
		}
	
	/**
	 * @throws ServletException 
	 * @see HttpServlet#HttpServlet()
	 */
	public SingleFileUploadServlet(String apiUrl, String apiKey) throws ServletException {
	
	this.apiUrl = apiUrl;
	this.apiKey = apiKey;
	this.chatHistory = "";
	
	}
	


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String prompt = request.getParameter("option");

		String message = "";
		String message2 = "";
		try {
			Part part = request.getPart("fname");
			String contetDisposition = part.getHeader("content-disposition");
			System.out.println(contetDisposition);
			part.write(getFileName(part));

			message = "Your File " + getFileName(part) + " has been uploaded succesfully!";

		} catch (Exception e) {
			message = "Error uploading file: " + e.getMessage();
		}

		// =============================================================================================//
		Part part = request.getPart("fname");
		String fileName = getFileName(part);

		SingleFileUploadServlet mm = new SingleFileUploadServlet("https://api.openai.com/v1/chat/completions", "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");
		String yamlFile = "C:/SivaTempFiles/singlefile_upload/" + fileName;
		String yamlContents = mm.readFile(yamlFile);

		try {
			// delete the file if already exisits
			File file = new File("C:\\SivaTempFiles\\singlefile_upload\\" + fileName + ".feature");
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {

		}
		String chatGPTOutput = "";
		String qr = "";

		HashMap<String, String> queries1 = JsonReader.getMapFromJson(
				"C:\\Users\\INE12364222\\OneDrive - Tesco\\Documents\\projects\\MyWebApplication\\src\\resources\\queries.json",
				prompt);
		for (int i = 1; i <= queries1.size(); i++) {
			qr = queries1.get("query" + i);
			if (i == 1) {
				qr = qr + yamlContents;
			} else {
				// qr=chatGPTOutput+qr+yamlContents;
				qr = qr + yamlContents;
			}

			// System.out.println("query======"+qr);

			chatGPTOutput = mm.getResponse(qr);
			mm.writeFile("C:/SivaTempFiles/singlefile_upload/" + fileName + ".feature", chatGPTOutput);

		}
		message2 = "C:/SivaTempFiles/singlefile_upload/" + fileName + ".feature";

		if (chatGPTError != null) {

			message = chatGPTError;
			request.setAttribute("message", message);
			request.getRequestDispatcher("message1.jsp").forward(request, response);

		} else {

			request.setAttribute("message", message);
			request.setAttribute("message2", message2);
			request.getRequestDispatcher("message.jsp").forward(request, response);

		}

		// ===========================================================================================//

	}

	private String getFileName(Part part) {
		String contetDisposition = part.getHeader("content-disposition");
		if (!contetDisposition.contains("filename=")) {
			return null;
		}

		int beginIndex = contetDisposition.indexOf("filename=") + 10;
		int endIndex = contetDisposition.length() - 1;

		return contetDisposition.substring(beginIndex, endIndex);
	}

	@SuppressWarnings("unchecked")
	public Object createJsonObject(String qr) {
		JSONObject jObject = new JSONObject();
		jObject.put("model", "text-davinci-003");
		jObject.put("prompt", chatHistory + qr);
		jObject.put("max_tokens", 1000);
		jObject.put("temperature", 0.7);
		jObject.put("top_p", 1.0);
		jObject.put("frequency_penalty", 0.0);
		jObject.put("presence_penalty", 0.0);

		return jObject;
	}

	private String readFile(String filePath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			sb.append(line).append(System.lineSeparator());
			line = reader.readLine();
		}
		reader.close();
		return sb.toString();
	}

	private void writeFile(String filePath, String contents) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

		writer.append(' ');
		writer.append(contents);

		writer.close();
	}

	public String getResponse(String qry) {

		// GIVEN
		request = RestAssured.given().baseUri(apiUrl).header("Authorization", "Bearer " + apiKey)
				.header("Content-Type", "application/json").body(createJsonObject(qry));
		// WHEN
		response = request.when().post();
		if (!(response.statusCode() == 200)) {

			chatGPTError = response.getBody().asString();
		}
		// THEN
		// response.then().assertThat().statusCode(200);
		JsonPath jsonPathObject = response.jsonPath();
		jsonPathObject.getString("choices[0].text");

		return jsonPathObject.getString("choices[0].text");
	}
}
