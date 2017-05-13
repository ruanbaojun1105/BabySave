package mybaby.models;

import java.io.Serializable;

//注册参数
public class Register implements Serializable{
	String displayname;// 昵称
	String mobile;// 手机号
	int CountryCode;// 用户所在国家
	String userpass;// 密码
	String smscode;// 验证码
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getCountryCode() {
		return CountryCode;
	}
	public void setCountryCode(int countryCode) {
		CountryCode = countryCode;
	}
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getSmscode() {
		return smscode;
	}
	public void setSmscode(String smscode) {
		this.smscode = smscode;
	}
	public Register(String displayname, String mobile, int countryCode,
			String userpass, String smscode) {
		super();
		this.displayname = displayname;
		this.mobile = mobile;
		CountryCode = countryCode;
		this.userpass = userpass;
		this.smscode = smscode;
	}
	public Register() {
		super();
	}
	

}
