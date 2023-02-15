It seems like all Soap services handle errors the same way, which is responding to the initial client call with either one of the two following XML payloads :

#Route rror handler :
	<ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
		<ges:Service_Name>{fn:data($inbound/@name)}</ges:Service_Name>
		<ges:Operation_Name>{fn:data($inbound/ctx:service/ctx:operation)}</ges:Operation_Name>
		<ges:Fault>{$fault/*}</ges:Fault>
		<ges:Request>{$request/*}</ges:Request>
		<ges:Response>{$body/*}</ges:Response>
	</ges:Erreur>

# Service error handler :
	<ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
		<ges:Service_Name>{fn:data($inbound/@name)}</ges:Service_Name>
		<ges:Operation_Name>{fn:data($inbound/ctx:service/ctx:operation)}</ges:Operation_Name>
		<ges:Fault>{$fault/*}</ges:Fault>
		<ges:Request>{$body/*}</ges:Request>
	</ges:Erreur>



# The simple xPath expression to extract the above xml errors schema from the .Piepline file  (namespace agnostic) is :
	/pipelineEntry/router/pipeline/stage/actions/route/outboundTransform/replace/expr/xqueryText
	OR :
	/*[local-name()='pipelineEntry']/*[local-name()='router']/*[local-name()='pipeline'][1]/*[local-name()='stage']/*[local-name()='actions']/*[local-name()='route']/*[local-name()='outboundTransform']/*[local-name()='replace']/*[local-name()='expr']/*[local-name()='xqueryText']

//con:pipelineEntry/con:router/con:pipeline[1]/con:stage/con:actions/con1:route/con1:outboundTransform/con2:replace/con2:expr/con:xqueryText

xmlns:con="http://www.bea.com/wli/sb/pipeline/config" xmlns:con1="http://www.bea.com/wli/sb/stages/publish/config"  xmlns:con2="http://www.bea.com/wli/sb/stages/transform/config"

-------------------------------------------------------------


[root@ocp-svc Error_Handling]# cat WSDL_Recette/WS_4G_ACTIVATE_V1/WS_4G_ACTIVATE_PS_V1.Pipeline | xmllint  --xpath "/*[local-name()='pipelineEntry']/*[local-name()='router']/*[local-name()='pipeline'][1]/*[local-name()='stage']/*[local-name()='actions']/*[local-name()='route']/*[local-name()='outboundTransform']/*[local-name()='replace']/*[local-name()='expr']/*[local-name()='xqueryText']/text()" -



cat list_Projet_OSB.txt  | while read prj_osb; do
cat WSDL_Recette/$prj_osb/*.Pipeline | xmllint  --xpath "/*[local-name()='pipelineEntry']/*[local-name()='router']/*[local-name()='pipeline'][1]/*[local-name()='stage']/*[local-name()='actions']/*[local-name()='route']/*[local-name()='outboundTransform']/*[local-name()='replace']/*[local-name()='expr']/*[local-name()='xqueryText']/text()" - > ./xml_errors/${prj_osb}.err.xml
done

-----------------------------------
# Example d'erreur réel from LOG_PROD.txt :
# Example 1 :
		<soapenv:Body xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
		  <ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
			<ges:Service_Name>ProxyService$InfoClientEboutique_V2$InfoClientEboutique_PS_V2</ges:Service_Name>
			<ges:Operation_Name>getInfoClientByND</ges:Operation_Name>
			<ges:Fault>
			  <errorCode xmlns="http://www.bea.com/wli/sb/context">OSB-380002</errorCode>
			  <reason xmlns="http://www.bea.com/wli/sb/context">Tried all: 1 addresses, but could not connect over HTTPS to server: 10.100.0.77 port: 8443</reason>
			  <location xmlns="http://www.bea.com/wli/sb/context">
				<node>RouteTo_InfoClientEboutique_BS_V2.0</node>
				<path>request-pipeline</path>
			  </location>
			</ges:Fault>
			<ges:Request>
			  <ns2:getInfoClientByND xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns2="http://impl.ws.wsgold.iam.ma/">
				<nd>212624447724</nd>
			  </ns2:getInfoClientByND>
			</ges:Request>
			<ges:Response>
			  <ns2:getInfoClientByND xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns2="http://impl.ws.wsgold.iam.ma/">
				<nd>212624447724</nd>
			  </ns2:getInfoClientByND>
			</ges:Response>
		  </ges:Erreur>
		</soapenv:Body>
	
# Example 2 :
		<soapenv:Body xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
		  <ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
			<ges:Service_Name>ProxyService$ServiceOperater_V2$ServiceOperater_PS_V2</ges:Service_Name>
			<ges:Operation_Name>ReadAllServices</ges:Operation_Name>
			<ges:Fault>
			  <errorCode xmlns="http://www.bea.com/wli/sb/context">OSB-380002</errorCode>
			  <reason xmlns="http://www.bea.com/wli/sb/context">connect timed out</reason>
			  <location xmlns="http://www.bea.com/wli/sb/context">
				<node>RouteTo_ServiceOperater_BS_V1.0</node>
				<path>response-pipeline</path>
			  </location>
			</ges:Fault>
			<ges:Request>
			  <ser:ReadAllServices xmlns:ser="http://services.webservice.ws.iam.ma/">
				<!--Optional:-->
				<USERNAME>WSUSER</USERNAME>
				<CO_ID>26042216</CO_ID>
			  </ser:ReadAllServices>
			</ges:Request>
			<ges:Response/>
		  </ges:Erreur>
		</soapenv:Body>
		
# Example 3 :
	<soapenv:Body xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	  <ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
		<ges:Service_Name>ProxyService$EnregistrerCommandeSAP_V1$EnregistrerCommandeSAP_PS_V1.1</ges:Service_Name>
		<ges:Operation_Name>ZsivSalesDataPushPackage</ges:Operation_Name>
		<ges:Fault>
		  <errorCode xmlns="http://www.bea.com/wli/sb/context">OSB-380001</errorCode>
		  <reason xmlns="http://www.bea.com/wli/sb/context">Internal Server Error</reason>
		  <location xmlns="http://www.bea.com/wli/sb/context">
			<node>RouteTo_EnregistrerCommandeSAP_V1.1</node>
			<path>response-pipeline</path>
		  </location>
		</ges:Fault>
		<ges:Request>
		  <ns2:ZsivSalesDataPushPackage xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ns2="urn:sap-com:document:sap:soap:functions:mc-style">
			<Zreturn/>
			<Zsivsalesdata>
			  <item>
				<Zseqnum>10020596</Zseqnum>
				<Zcodesiv>300</Zcodesiv>
				<Zcodeoffre>I0001414</Zcodeoffre>
				<Zqte>1</Zqte>
				<Zcodeagence>AA</Zcodeagence>
				<Zcoderetour>0</Zcoderetour>
				<Zdatevente>04062021</Zdatevente>
				<Zcodetypeclient>A</Zcodetypeclient>
				<Zdocnum>93631843</Zdocnum>
				<Zappelnum>TOINETJOSETTEMARIE2021</Zappelnum>
				<Zlogin>ma.houli</Zlogin>
				<Zliboffre>ADSL SANS PACK</Zliboffre>
				<Ztypeoffre>Packs NA</Ztypeoffre>
			  </item>
			</Zsivsalesdata>
		  </ns2:ZsivSalesDataPushPackage>
		</ges:Request>
		<ges:Response>
		  <env:Fault xmlns:env="http://www.w3.org/2003/05/soap-envelope">
			<env:Code>
			  <env:Value>env:Receiver</env:Value>
			</env:Code>
			<env:Reason>
			  <env:Text xml:lang="en">Web service processing error; more details in the web service error log on provider side (UTC timestamp 20221119000112; Transaction ID 6374EBE38FB30E50E10000000A640DA2)</env:Text>
			</env:Reason>
			<env:Detail/>
		  </env:Fault>
		</ges:Response>
	  </ges:Erreur>
	</soapenv:Body>
	
Example 4 :
	<soapenv:Body xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
	  <ges:Erreur xmlns:ges="http://xmlns.iam.ma/SOA/GestionErreurs">
		<ges:Service_Name>ProxyService$EnregistrerCommandeSAP_V1$EnregistrerCommandeSAP_PS_V1.1</ges:Service_Name>
		<ges:Operation_Name>ZsivSalesDataPushPackage</ges:Operation_Name>
		<ges:Fault>
		  <errorCode xmlns="http://www.bea.com/wli/sb/context">OSB-380000</errorCode>
		  <reason xmlns="http://www.bea.com/wli/sb/context">General runtime error: Broken pipe</reason>
		  <location xmlns="http://www.bea.com/wli/sb/context">
			<node>RouteTo_EnregistrerCommandeSAP_V1.1</node>
			<path>request-pipeline</path>
		  </location>
		</ges:Fault>
		<ges:Request>
		  <ns2:ZsivSalesDataPushPackage xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ns2="urn:sap-com:document:sap:soap:functions:mc-style">
			<Zreturn/>
			<Zsivsalesdata>
			  <item>
				<Zseqnum>6518382</Zseqnum>
				<Zcodesiv>300</Zcodesiv>
				<Zcodeoffre>I0001414</Zcodeoffre>
				<Zqte>1</Zqte>
				<Zcodeagence>WC</Zcodeagence>
				<Zcoderetour>0</Zcoderetour>
				<Zdatevente>08062020</Zdatevente>
				<Zcodetypeclient>D</Zcodetypeclient>
				<Zdocnum>91857212</Zdocnum>
				<Zappelnum>BOUTGAYOUTMOHAMED2020</Zappelnum>
				<Zlogin>r.naimi</Zlogin>
				<Zliboffre>ADSL SANS PACK</Zliboffre>
				<Ztypeoffre>Packs NA</Ztypeoffre>
				<Zsernumfrom>null</Zsernumfrom>
				<Zsernumto>null</Zsernumto>
			  </item>
			</Zsivsalesdata>
		  </ns2:ZsivSalesDataPushPackage>
		</ges:Request>
		<ges:Response>
		  <ns2:ZsivSalesDataPushPackage xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ns2="urn:sap-com:document:sap:soap:functions:mc-style">
			<Zreturn/>
			<Zsivsalesdata>
			  <item>
				<Zseqnum>6518382</Zseqnum>
				<Zcodesiv>300</Zcodesiv>
				<Zcodeoffre>I0001414</Zcodeoffre>
				<Zqte>1</Zqte>
				<Zcodeagence>WC</Zcodeagence>
				<Zcoderetour>0</Zcoderetour>
				<Zdatevente>08062020</Zdatevente>
				<Zcodetypeclient>D</Zcodetypeclient>
				<Zdocnum>91857212</Zdocnum>
				<Zappelnum>BOUTGAYOUTMOHAMED2020</Zappelnum>
				<Zlogin>r.naimi</Zlogin>
				<Zliboffre>ADSL SANS PACK</Zliboffre>
				<Ztypeoffre>Packs NA</Ztypeoffre>
				<Zsernumfrom>null</Zsernumfrom>
				<Zsernumto>null</Zsernumto>
			  </item>
			</Zsivsalesdata>
		  </ns2:ZsivSalesDataPushPackage>
		</ges:Response>
	  </ges:Erreur>
	</soapenv:Body>