package com.dd.ssm.model;

/**
 * 数据 修改日期  redis Key
 * 
 * @author dingpc
 * @date 2017年3月23日
 */
public class ModifyDateKey {
	
	private static final String BREAK_SIGN = ":";

	//知识点 修改时间 前缀
	private static final String KNOWLEDGE_MD = "KNOWLEDGE_MD:";
	//学科 前缀
	private static final String SCHOOL_LEVEL_SUBJECT = "SCHOOL_LEVEL_SUBJECT:";
	
	public static String getKnowledgeMDKey(String subject, Integer education) {
		return KNOWLEDGE_MD + subject + BREAK_SIGN + education;
	}
	
	public static String getSubjectMDKey(String schoolLevel){
		return SCHOOL_LEVEL_SUBJECT + schoolLevel;
	}
	
}
