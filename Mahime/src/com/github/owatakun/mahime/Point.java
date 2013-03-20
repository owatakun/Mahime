package com.github.owatakun.mahime;

public class Point {
	private int x, y, z;

	public Point(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX(){ return x;}
	public int getY(){ return y;}
	public int getZ(){ return z;}

	public String serialize(){
		return x + "," + y + "," + z;
	}

	public static Point deserialize(String str){
		//変数準備
		int x = 0, y = 0, z = 0;
		boolean error = false;
		//分割
		String[] data = str.split(",");
		//フォーマット適合確認
		if(data.length == 3){
			//変数へのデータの取り込み
			if (Util.tryIntParse(data[0])) {
				x = Integer.parseInt(data[0]);
			} else {
				error = true;
			}
			if (Util.tryIntParse(data[1])) {
				y = Integer.parseInt(data[1]);
			} else {
				error = true;
			}
			if (Util.tryIntParse(data[2])) {
				z = Integer.parseInt(data[2]);
			} else {
				error = true;
			}
		} else {
			error = true;
		}
		//うまく行ったらインスタンス化して返す
		if(error){
			return null;
		}else{
			return new Point(x, y, z);
		}
	}
}
