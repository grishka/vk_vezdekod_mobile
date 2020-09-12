package me.grishka.vezdekod.donate;

import java.util.Calendar;

public class Utils{
	public static String daysToDate(int day, int month, int year){
		Calendar calendar=Calendar.getInstance();
		calendar.set(year, month, day);
		long diff=calendar.getTimeInMillis()-System.currentTimeMillis();
		if(diff<=3600000*24)
			return "сегодня";
		int days=(int)Math.round((double)diff/(3600000.0*24.0));
		if(days/10%10==1)
			return "через "+days+" дней";
		int i=days%10;
		if(i==1)
			return "через "+days+" день";
		if(i>=2 && i<=4)
			return "через "+days+" дня";
		return "через "+days+" дней";
	}

	public static String formatCurrency(long amount){
		String n=String.valueOf(amount);
		if(n.length()>18)
			n=n.substring(0, 18);
		if(n.length()<3){
			return n+" ₽";
		}else{
			StringBuilder sb=new StringBuilder();
			int parts=n.length()/3;
			int firstLen=n.length()%3;
			if(firstLen>0){
				sb.append(n.substring(0, firstLen));
				sb.append(' ');
			}
			for(int i=0;i<parts;i++){
				sb.append(n.substring(firstLen+i*3, firstLen+i*3+3));
				sb.append(' ');
			}
			sb.append('₽');
			return sb.toString();
		}
	}
}
