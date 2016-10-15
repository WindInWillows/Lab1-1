import java.util.ArrayList;
import java.util.Scanner;

//v2
//b1-1

// 实现了一个处理多项式的类

/* 
 * 功能
 *  1、读入一个多项式表达式，并保存
 *  2、读入化简命令，对表达式进行化简操作，并输出
 *  3、读入求导命令，对表达式进行求导操作，并输出
 *  4、读入退出命令，退出系统
 */

/*
 *  错误信息
 *   1、Invalid character inside	输入包含除 数字、字母、空格、tab、+、-、*、^ 以外 的字符的表达式
 *   2、Wrong command			命令格式出错，在！前有除了空格和制表符之外的字符
 *   3、Unrecognized command		输入了未知的命令
 *   4、^ error. It should be var^num	对^的使用，不满足var^num模式
 *   5、Polynome hasn't been entered 	输入表达式前，输入化简和求导的命令
 */

/*
 * 输入的表达式说明
 *  1、只能包含 数字、字母、空格、tab、+、-、*、^ 等字符
 *  2、可以包含空格和制表符
 *  3、支持加减法
 *  4、可以在使用变量^数字，用来表示指数；不支持数字后加^和^后加字母
 *  5、变量名称可以是字母和数字的组合，长度无限制
 *  6、不支持括号
 *  7、不支持*的省略
 *  8、不支持同类项合并
 */

/*
 * 命令说明
 *  1、支持多变量化简
 *  2、支持化简的变量的值为负数
 *  3、不支持小数
 *  4、化简命令中出现未在表达式中出现的变量则忽略
 *  5、化简命令中出现类似于!simplify x y=1,则忽略x
 *  6、化简命令中出现错误的表达形式如 x=x x=pi x=1.5,则忽略这一项
 *  7、求导命令中出现未在表达式中出现的变量则返回0
 *  8、不支持同类项合并
 */
public class Polynome {

	//--------------------------------------------------------
	// 一个输入流对象，用于接受表达式和字符串的输入
	private Scanner scan;
	// 通过使用一个类型为Item的列表保存当前表达式，是主要的
	private ArrayList<Item> expressionArray = new ArrayList<Item>();
	// 临时字符串，用于保存去除空格和制表符之后的输入字符串
	private String tmpStr = "";
	
	// opCode为操作码，opStr为操作字。这两个变量在getInput被修改，作用于impOperation。
	// opCode决定在impOperation中执行什么类型的操作
	// opStr辅助opCode的实现
	//	执行化简命令（!simplify x=1 y=2）时，opStr中保存 x=1 y=2;
	//	执行求导命令(!d/d x)时，opStr中保存x;
	//	输出错误信息是，opStr保存错误信息。
	private int opCode = 0;
	private String opStr = "";
	// 字符串常量
	private static final String EXIT_FLAG = "exit";
	private static final String SIMPLIFY = "simplify";
	private static final String DIFF = "d/d";
	//--------------------------------------------------------
	
	public Polynome() {
		//??
		scan = new Scanner(System.in);
	}

	public static void main(String[] args) {
		Polynome po = new Polynome();
		// 打印提示信息
		po.prompt();
		while(true){
			//读取一个字符串，判断是表达式还是命令，并做相应的处理，以及得到一个操作码
			po.getInput();
			//根据上一步中获得的操作码执行相应的操作
			po.impOperation();
		}
	}
	
	public void prompt(){
		// 系统开始运行后打印提示信息
		// 可在此添加包括版本，帮助等的提示信息
		System.out.println("<Welcome to Polynomials System 1.0>");
	}
	
	
	public void getInput(){
		
		// 读取一个字符串
		System.out.print(">> ");
		String strInput = scan.nextLine();
		
		// 当输入空时，则重新输入
		if (strInput.isEmpty()){
			opCode = 0;
			return;
		}
		
		// 此字符串中不包含“！”，则为表达式；否则，为命令。
		// 并进行进入相应的处理函数
		if (strInput.indexOf("!")==-1) expression(strInput);
		else command(strInput);
  	}
	
	public void impOperation() {
		
		// 根据操作码执行相应操作	
		switch(opCode) {
			case -1: exitSys(); break; 	// 退出系统
			case 0:				break; 	// 空操作,默认值
			case 1:	print();    break;	// 打印当前表达式
			case 2:	simplify(); break;	// 化简表达式
			case 3:	derivative();break; // 对表达式求导
			case 4:	errorOutput();break;// 输出错误信息，错误信息保存在opStr中
			default: 	        break;  // 空操作
		}
	}
	
	
	private void expression(String strInput){
		
		// 对输入 StrInput 的合法性进行判定，同时去除表达式中的空格和制表符
		if (validateExpressionAndStrip(strInput) == false){
			opCode = 4;
			opStr  = "Invalid character inside";
			return;
		}
		
		// 根据+和-，对表达式对应的字符串进行拆分
		// 并以拆分后的各个字符串作为参数，分别建立Item对象，加入到expressionArray中
		buildItem();
	}
	
	
	private boolean validateExpressionAndStrip(String str){
		// 对 tmpStr 进行初始化
		tmpStr = "";
		
		char[] chars = str.toCharArray();
		for(char ch : chars){
			// 包含除 数字、字母、空格、tab、+、-、*、^ 以外 的字符
			if(ch != ' ' && ch != '\t' ){
				if(!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') 
				|| (ch >= 'A' && ch <= 'Z') || ch == '+' || ch == '-'
				||  ch == '*' || ch == '^')){
					return false;
				}
				tmpStr += ch;
			}
		}
		return true;
	}
	
	private void buildItem(){
		// 对存储表达式的列表进行初始化
		expressionArray.clear();
		
		// 将一个表达式字符串根据+、-,进行拆分
		// 第一项的符号默认为正
		String itemStr = "+";
		char[] chars = tmpStr.toCharArray();
		for (char ch : chars){
			if (ch == '+'){
				if (addNewItem(itemStr) == false) return;
				// 下一项的符号为正
				itemStr = "+";
			}
			else if (ch == '-'){
				if (addNewItem(itemStr) == false) return;
				// 下一项的符号为负
				itemStr = "-";
			}
			else itemStr += ch;
		}
		if (addNewItem(itemStr) == false) return;
		// 将opCode的值置为1,表示读入表达式成功，在impOperation中,对表达式进行打印
		else opCode = 1;
	}
	
	
	private boolean addNewItem(String itemStr){
		Item newItem = (new Item(itemStr));
		// 处理itemStr时，出错
		if(newItem.errorFlag == true) {
			opCode = 4;
			opStr = "^ error. It should be var^num";
			return false;
		}
		expressionArray.add(newItem);
		return true;
	}
	
	private void command(String strInput){
		// 检查输入的命令的合法性
		if (validateCommandAndStrip(strInput) == false)
		{
			opCode = 4;
			opStr = "Wrong command";
			return;
		}
		
		// 判断命令类型,并修改opCode和opStr
		getCommandType();
	}
	
	private boolean validateCommandAndStrip(String str){
		// 检查输入的命令是否在！前有除空格和制表符之外的字符
		// 如果有，则说明输入的命令有误
		char[] chars = str.toCharArray();
		boolean checkFlag = true;
		for (char ch : chars){
			if(ch == '!'){
				checkFlag = false;
				break;
			}
			else if((ch != ' ' && ch != '\t') && checkFlag){
				return false;
			}
		}
		// !!! 可添加功能 去除空格
		tmpStr = str;
		return true;
	}

	
	private void getCommandType(){

		int index;
		if ((index = tmpStr.indexOf(SIMPLIFY)) != -1) {
			index += SIMPLIFY.length() + 1;
			opCode = 2;//化简表达式
		}
		else if((index = tmpStr.indexOf(DIFF)) != -1) {
			index += DIFF.length() + 1;
			opCode = 3;//表达式求导
		}
		else if((tmpStr.indexOf(EXIT_FLAG) != -1)){
			opCode = -1;//退出程序
			return;
		}
		else {
			opCode = 4;//命令错误
			opStr = "Unrecognized command";
			return;
		}
		if (!this.expressionArray.isEmpty()) opStr = tmpStr.substring(index);
		else{
			opCode = 4;
			opStr="Polynome hasn't been entered";
		}
	}
	
	private void simplify() {
		String resStr = "";
		boolean firstFlag = true;//也许能优化
		
		for (int i=0; i<this.expressionArray.size();i++){
			resStr += this.expressionArray.get(i).simplify(opStr).toString(firstFlag);
			firstFlag = false;
		}
		System.out.println(resStr);
	}

	private void derivative() {
		String resStr = "";
		boolean firstFlag = true;//也许能优化
		for (int i=0; i<this.expressionArray.size();i++){
			Item diffItem = this.expressionArray.get(i).diff(this.opStr);
			if (diffItem == null) resStr += "";
			else {
				resStr += diffItem.toString(firstFlag);
				firstFlag = false;
			}
		}
		if (resStr.isEmpty()) resStr = "";
		System.out.println(resStr);
	}
	

	private void exitSys() {
		System.out.println("Thanks for use!");
		System.exit(0);
	}
	
	private void errorOutput(){
		System.out.println("Error: "+opStr+".");
	}
	
	private void print() {
		String resStr = "";
		boolean firstFlag = true;//也许能优化
		for (int i=0; i<this.expressionArray.size();i++){
			resStr+=this.expressionArray.get(i).toString(firstFlag);
			firstFlag = false;
		}
		System.out.println(resStr);
	}
	
}

