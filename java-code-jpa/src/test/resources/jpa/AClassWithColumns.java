package jpa;

@Entity
public class AClassWithColumns {

	private long id;
	private int count;
	@NotNull
	private String name;
	private String title;

}