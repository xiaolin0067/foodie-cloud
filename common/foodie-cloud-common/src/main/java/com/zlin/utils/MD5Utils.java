package com.zlin.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MD5Utils {

	/**
	 * 对字符串进行md5加密
	 */
	public static String getMd5Str(String strValue) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
		return newstr;
	}

	public static void main(String[] args) {
		try {
			String md5 = getMd5Str("imooc");
			System.out.println(md5);
//			generateEsTestDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateEsTestDocument() {
		Random random = new Random();
		String[] uname = {"恨桃","依秋","依波","香巧","紫萱","涵易","忆之","幻巧","美倩","安寒"};
		String[] nname = {"长卿","羽涅","辛夷","青黛","半夏","剪秋","沉香","君迁子","荆芥","枳实"};
		String[] desc = {"人生来就是孤独的，不要奢望能够依靠谁","是福是祸都得面对，是好是坏都会过去","人生如棋，步步相随",
				"人来到世界上，没钱并不可怕，怕的是一直要等人来救济","生病并不可怕，怕的是一病不起","人生最可悲的事情，莫过于胸怀大志，却又虚度光阴",
				"在薄情的世界里，每个人都来去匆匆","倒不如学会好好爱自己，不辜负余生","低头走路的人只看到大地的厚重，却忽略了高空的高远",
				"每个人身上都有太阳，只是要让它发出光来"};
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long dayMs = 86400000;
		long nowMs = System.currentTimeMillis();
		byte[] bytes = {0, 1};
		RestTemplate restTemplate = new RestTemplate();
		for (int i = 0; i < 25; i++) {
			EsDTO dto = new EsDTO();
			dto.setId(i);
			dto.setAge(18+i);
			dto.setUsername(uname[random.nextInt(10)]);
			dto.setNickname(nname[random.nextInt(10)]);
			dto.setDesc(desc[random.nextInt(10)]);
			dto.setSex(bytes[random.nextInt(2)]);
			dto.setBirthday(sdf.format(new Date(nowMs - (dayMs *  i))));
			dto.setFace("https://www.imooc.com/static/img/index/logo.png");
			System.out.println(JsonUtils.objectToJson(dto));
			ResponseEntity<String> result = restTemplate.postForEntity("http://192.168.3.26:9200/shop/_doc/" + (1001 + i),new HttpEntity<>(dto),String.class);
			System.out.println(result.getBody());
		}
	}
}

class EsDTO {
	long id;
	int age;
	String username;
	String nickname;
	String desc;
	byte sex;
	String birthday;
	String face;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}
}