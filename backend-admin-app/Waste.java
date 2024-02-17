/*
 * This class represents a Waste whose properties and classification is noted.
 

public class Waste implements Comparable<Waste> {
	
	private String name;
	private String material;
    private String category;
	
	public Waste(String name, String material, String category) {
		this.name = name;
		this.material = material;
        this.category = category;
	}

    public String getName() {
	return name;
    }

    public String getMaterial() {
	return material;
    }

    public String getCategory() {
	return category;
    }
	
	public String toString() {
		return "Waste name: " + name + " is " + category;
	}
	
    /*
	@Override
	public boolean equals(Object o) {
		if (o == null || (o instanceof Waste) == false) return false;
		Product p = (Product)o;
		return p.id.equals(this.id) && p.status.equals(this.status);
	}
	
	@Override
	public int compareTo(Product p) {
		if (this.equals(p)) return 0;
		else {
			return this.id.compareTo(p.id);
		}
	}
	
	@Override
	public int hashCode() {
		return id.hashCode() + status.hashCode();
	}
}    
*/

