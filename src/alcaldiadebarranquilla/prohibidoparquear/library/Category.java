package alcaldiadebarranquilla.prohibidoparquear.library;

public class Category {

	private String categoria;
	private int id;

	public Category(String categoria, int id) {
		super();
		this.categoria = categoria;
		this.id = id;
	}

	public Category() {
		super();
		this.id = 0;
		this.categoria = "";
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return categoria;
	}

}
