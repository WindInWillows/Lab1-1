//v2
//b1-1
//c4-1

public class Node {
	public Node(String v,int power) {
		this.power = power;
		this.v = v;
	}

	public Node() {
	}

	public int power;
	public String v;
	
	public Node(String v) {
		this.power = 1;
		this.v = v;
	}
	
	public void inc(int power) {
		this.power += power;
	}
	
	public void dec() {
		this.power--;
	}
	
	public Node getCopy(){
		Node res = new Node();
		res.power = this.power;
		res.v = this.v;
		return res;
	}
	
	public boolean equals(Node n) {
		return n.v.equals(this.v)
		&& n.power == this.power;
	}
}

