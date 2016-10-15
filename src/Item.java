import java.util.ArrayList;

//v2
//b2-1
//b1-1
//c4-1
>>>>>>> 1140310118

public class Item {
	public int coe=1;
	private ArrayList<Node> item = new ArrayList<Node>();
	public boolean errorFlag = false;
	/*
	 *输入表达式项和项的正负，建立起表达式项的数据结构：
	 *	表达式项的变量部分由一个ArrayList组成
	 *	表达式的系数包含正负
	 */
	//'-','11*x*y*x*8' => -88,Node(x,2),Node(y,1)
	public Item(String exp, boolean isPositive) {
		////处理系数的正负问题
		if( !isPositive){
			this.coe *= -1;
		}
		////拆分一个多项式中一项
		//将'11*x*y*x*8'拆分为   列表[11 x y x 8]
		String[] tmp_list=exp.split("\\*");
		for(int i=0;i<tmp_list.length;i++){
			String tmp_i=tmp_list[i];
			Integer num=isNumber(tmp_i);
			
			Integer power = 1;
			if(tmp_i.indexOf("^") != -1) {
				String[] tmmp = tmp_i.split("\\^");
				power=isNumber(tmmp[1]);
				if (power == null ){
					errorFlag = true;
					return;
				}
				if (isNumber(tmmp[0]) != null ){
					errorFlag = true;
					return;
				}
				tmp_i = tmmp[0];
			}
			
			// num=null 表示tmp_i不是数字，否则返回这个数字（int），将这个数字直接与系数相乘即可
			if( num!= null ){
				this.coe *= num;
			}
			// tmp_i 为变量，在下面的代码中判断变量tmp_i是否出现过
			else{
				//判断字符tmp_i是否已经出现的标志
				boolean appear_flag=false;// ！也许能被优化
				//遍历tmp_list中已经处理的部分，判断变量tmp_i是否出现过
				for(int j=0;j<this.item.size();j++){
					Node Node_j=this.item.get(j);
					// 变量tmp_i出现在之前处理的字符串中，对原变量对应节点的指数+1
					if(Node_j.v.equals(tmp_i)){
						Node_j.inc(power);
						appear_flag=true;
					}
				}
				//添加未出现的变量
				if (appear_flag==false){
					this.item.add(new Node(tmp_i, power));
				}
			}
		}
	}
	
	public Item() {}

	public Item(String itemStr) {
		this(itemStr.substring(1),itemStr.toCharArray()[0] == '+' );
	}

	public Item diff(String x) {
		Item tmp = getCopy();
		for(int i=0;i<tmp.item.size();i++){
			if (tmp.item.get(i).v.equals(x)){
				tmp.coe *= tmp.item.get(i).power;
				tmp.item.get(i).dec();
				return tmp;
			}
		}
		return null;
	}
	private Item getCopy(){
		// 也许能优化
		Item res = new Item();
		res.coe = this.coe;
		for(int i=0;i<this.item.size();i++){
			res.item.add(this.item.get(i).getCopy());
		}
		return res;
	}
	
	//输出当前项
	public String toString(Boolean firstFlag) {
		String str = "";
		if (coe == 0) return "";
		if(coe > 0 && !firstFlag)
			str += "+";
		str += coe;
//		if(coe!=1) str += coe;
//		else if(item.isEmpty()) str+=coe;
		for(Node n : item) {
			if(n.power > 1 && !n.v.equals("")){
				if(str.length() > 0) str += "*";
				str += n.v+"^"+n.power;
			}
			else if(n.power == 1 && !n.v.equals("")){
				if(str.length() > 0) str += "*";
				str += n.v;
			}
		}
		return str;
	}
	
	//判断两项是否能合并
	private boolean equals(Item it) {
		ArrayList<Node> arr = it.item;
		for(Node n : arr){
			if(!this.hasNode(n))
				return false;
		}
		return true;
	}
	
	//判断该项中是否有此变量
	private boolean hasNode(Node n) {
		for(Node nn:item){
			if(nn.equals(n))
				return true;
		}
		return false;
	}
	
	private boolean hasVarialbe(Node n) {
		for(Node nn:item){
			if(nn.v.equals(n.v))
				return true;
		}
		return false;
	}
	
	private Node hasVarialbe(String v) {
		for(Node nn:item){
			if(nn.v.equals(v))
				return nn;
		}
		return null;
	}	
	
	//为表达式赋值，支持多个变量赋值
	//返回null说明找不到
	public Item simplify(String value) {
		Item tmp = getCopy();
		
		String[] strArray = {value};
		if (value.indexOf(" ") != -1){
			strArray = value.split(" ");
		}
		for (String tmpStr : strArray){
			if (tmpStr.indexOf("=") != -1){
				String[] ss = tmpStr.split("=");
				String name = ss[0];
				Integer v = isNumber(ss[1]);
				if (v == null) continue;
				Node n = null;
				if((n = tmp.hasVarialbe(name)) != null){
					tmp.coe *= (int)Math.pow(v, n.power);
					tmp.item.remove(n);
				}
			}
		}
		return tmp;
	}
	
	private Integer isNumber(String s) {
		Integer tmp = null;
		try{
			 tmp = Integer.parseInt(s);
		} catch(NumberFormatException e){
			return null;
		}
		return tmp;
	}
}
