package test;

@Entity
@Table(name = "TABLE_DIFF_COLUMNS")
public class DifferentColumns {

	@Column(name = "ID")
	public int id;
	@Column(name = "NAME")
	public String name;

}