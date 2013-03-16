package com.github.owatakun.mahime;

public class Util {
	/**
	 * フォーマットコードの変換(&→§)
	 * @param code 変換したいフォーマットコード(&a)
	 * @return 変換されたフォーマットコード(§a)
	 */
	public static String repSec(String code) {
		code = code.replaceAll("&([0-9a-fk-r])", "\u00A7$1");
		return code;
	}

	/**
	 * 整数値への変換が可能かをチェック
	 * @param src チェック対象の文字列(123)
	 * @return チェック結果
	 */
	public static boolean tryIntParse(String src) {
		return src.matches("^-?[0-9]+$");
	}
}
