package com.ideacode.news.net;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.ideacode.news.app.AppException;
import com.ideacode.news.common.util.CommonSetting;

/**
 * <p>
 * FileName: SoapWebServiceUtil.java
 * </p>
 * <p>
 * Description: cxf��ʽ��webservice���ݴ��乤����
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * @author Vic Su
 * </p>
 * <p>
 * @content andyliu900@gmail.com
 * </p>
 * <p>
 * @version 1.0
 * </p>
 * <p>
 * CreatDate: 2012-10-25 ����3:51:42
 * </p>
 * <p>
 * Modification History
 * </p>
 */
@SuppressWarnings("all")
public class SoapWebServiceUtil { 
    static private Log log = LogFactory.getLog(SoapWebServiceUtil.class.getName());

    /** ����������ռ� */
    private static String NAMESPACE = "http://webservice.cxf.msg.net.cn/";
    /** �������� */
    private static String SERVICE_NAME = null;
    private static String METHOD_NAME = null;
    private static HashMap PROPERTYS = null;

    public SoapWebServiceUtil(String service_name, String method_name, HashMap propertys) {
        this.SERVICE_NAME = service_name;
        this.METHOD_NAME = method_name;
        this.PROPERTYS = propertys;
    }

    public SoapObject getRespondData() throws AppException{
        SoapObject result = null;
        try {
            // ���õķ���
            String methodName = METHOD_NAME;
            // ����httpTransportSE�������
            HttpTransportSE ht = new HttpTransportSE(CommonSetting.WebServiceUrl + SERVICE_NAME);
            ht.debug = true;
            // ʹ��soap1.1Э�鴴��Envelop����
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // ʵ����SoapObject����
            SoapObject request = new SoapObject(NAMESPACE, methodName);
            if (PROPERTYS != null) {
                Object[] keySets = PROPERTYS.keySet().toArray(); // ��ô�������HashMap��keyֵ
                for (int i = 0; i < keySets.length; i++) {
                    request.addProperty(keySets[i].toString(), PROPERTYS.get(keySets[i].toString()));
                }
            }
            // ��SoapObject��������ΪSoapSerializationEnvelope����Ĵ���SOAP��Ϣ
            envelope.bodyOut = request;
            // ����webService
            ht.call(null, envelope);
            if (envelope.getResponse() != null) {
                result = (SoapObject) envelope.getResponse();
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            throw AppException.soap(e);            
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            throw AppException.xml(e);
        }
        return result;

    }
    
    /**
     * ���ݷ������˷��ط�byte[]�������ݣ���String��int��
     * @return
     */
    public Object getObjectRespondData() throws AppException{
    	Object result = null;
        try {
            // ���õķ���
            String methodName = METHOD_NAME;
            // ����httpTransportSE�������
            HttpTransportSE ht = new HttpTransportSE(CommonSetting.WebServiceUrl + SERVICE_NAME);
            ht.debug = true;
            // ʹ��soap1.1Э�鴴��Envelop����
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            // ʵ����SoapObject����
            SoapObject request = new SoapObject(NAMESPACE, methodName);
            if (PROPERTYS != null) {
                Object[] keySets = PROPERTYS.keySet().toArray(); // ��ô�������HashMap��keyֵ
                for (int i = 0; i < keySets.length; i++) {
                    request.addProperty(keySets[i].toString(), PROPERTYS.get(keySets[i].toString()));
                }
            }
            // ��SoapObject��������ΪSoapSerializationEnvelope����Ĵ���SOAP��Ϣ
            envelope.bodyOut = request;
            // ����webService
            ht.call(null, envelope);
            if (envelope.getResponse() != null) {
                result = (Object) envelope.getResponse();
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            throw AppException.soap(e);            
        } catch (IOException e) {
            e.printStackTrace();
            throw AppException.io(e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            throw AppException.xml(e);
        }
        return result;
    }
}
