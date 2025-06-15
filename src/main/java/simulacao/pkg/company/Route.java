package simulacao.pkg.company;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tudresden.sumo.objects.SumoStringList;

public class Route {

	private static NodeList nodeList;

	private SumoStringList edges;
	private String idRoute;
	private boolean acabou;

	public Route(int idRoute) {
		try {
			if (nodeList == null) {
				importXML("data/rotas.xml");
			}

			this.idRoute = "route" + idRoute;
			extairEdges(1);
			this.acabou = false;
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importXML(String uriXML) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document documento = builder.parse(uriXML);
		nodeList = documento.getElementsByTagName("vehicle");
	}

	private void extairEdges(int numberID) throws Exception {

		Node nodeVehicle = nodeList.item(numberID - 1);

		Element elem = (Element) nodeVehicle;
		Node nodeRoute = elem.getElementsByTagName("route").item(0);
		Element auxedges = (Element) nodeRoute;
		String route = auxedges.getAttribute("edges");

		edges = new SumoStringList();
		edges.clear();

		for (String e : route.split(" ")) {
			edges.add(e);
		}
	}

	public String getIdRoute() {
		return idRoute;
	}

	public SumoStringList getEdges() {
		return edges;
	}

	public void finish() {
		this.acabou = true;
	}

	public boolean acabou() {
		return acabou;
	}
}