package shapes;

import vector.Vector;

public class Edge {
	
	/**
	 * Determines the common endpoint of a group of edges, if there is one.
	 * @param edges the group of edges
	 * @return the common endpoint, or null if there isn't one
	 */
	public static Vector getCommonEndpoint(Edge...edges) {
		for (var endpoint : edges[0].endpoints) {
			boolean foundCommonEndpoint = true;
			for (int i = 1; i < edges.length; i++) {
				if (!edges[i].containsVertex(endpoint)) {
					foundCommonEndpoint = false;
					break;
				}
			}
			if (foundCommonEndpoint) return endpoint;
		}
		return null;
	}
	
	public final Vector v1, v2;
	public final Vector[] endpoints;
	
	public Edge(Vector v1, Vector v2) {
		this.v1 = v1;
		this.v2 = v2;
		endpoints = new Vector[] { v1, v2 };
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Edge) {
			Edge e = (Edge) o;
			return v1 == e.v1 && v2 == e.v2 || v1 == e.v2 && v2 == e.v1;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// ordering of v1 and v2 does not matter, and this is reflected
		// by adding their hashcodes 
		return v1.hashCode() + v2.hashCode();
	}
	
	public boolean isConnected(Edge e) {
		if (equals(e)) return false;
		return v1 == e.v1 || v1 == e.v2 || v2 == e.v1 || v2 == e.v2;
	}
	
	public boolean containsVertex(Vector endpoint) {
		return v1 == endpoint || v2 == endpoint;
	}
}