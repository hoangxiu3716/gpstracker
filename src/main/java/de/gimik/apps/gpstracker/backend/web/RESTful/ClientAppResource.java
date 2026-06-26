package de.gimik.apps.gpstracker.backend.web.RESTful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import com.turo.pushy.apns.ApnsClient;
//import com.turo.pushy.apns.ApnsClientBuilder;
//import com.turo.pushy.apns.PushNotificationResponse;
//import com.turo.pushy.apns.auth.ApnsSigningKey;
//import com.turo.pushy.apns.util.ApnsPayloadBuilder;
//import com.turo.pushy.apns.util.SimpleApnsPushNotification;
//import com.turo.pushy.apns.util.TokenUtil;
//import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;

import de.gimik.apps.gpstracker.backend.util.Constants;
import de.gimik.apps.gpstracker.backend.util.ServerConfig;
import de.gimik.apps.gpstracker.backend.web.viewmodel.ResultObjecttInfo;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path("/clientapp")
public class ClientAppResource {

	@Autowired
	private ServerConfig serverConfig;
//	private SimpleApnsPushNotification pushNotification;
//	@Path("pushdata")
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public ResultObjecttInfo index(){
//		try {
//			final ApnsClient apnsClient = new ApnsClientBuilder()
//			        .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
//			        .setSigningKey(ApnsSigningKey.loadFromPkcs8File(new File("E:\\CTY\\2018\\Blupassion\\src\\main\\resources\\AuthKey_PDTXBBFKGF.p8"),
//			                "TCT8K63NTZ", "PDTXBBFKGF"))
//			        .build();
//			
//			final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
//		    payloadBuilder.setAlertBody("Example!");
//
//		    final String payload = payloadBuilder.buildWithDefaultMaximumLength();
//		    final String token = TokenUtil.sanitizeTokenString("79bf0c2743ac5ad2497ef4977794d6a6978d5e6472b2da19ce2bc78dcc244b02");
//
//		    pushNotification = new SimpleApnsPushNotification(token, "com.ikssoftware.Eaton.ENT-IKS", payload);
//		    final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
//		    sendNotificationFuture = apnsClient.sendNotification(pushNotification);
//
//		        PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse;
//				try {
//					pushNotificationResponse = sendNotificationFuture.get();
//					if (pushNotificationResponse.isAccepted()) {
//			            System.out.println("Push notification accepted by APNs gateway.");
//			        } else {
//			            System.out.println("Notification rejected by the APNs gateway: " +
//			                    pushNotificationResponse.getRejectionReason());
//
//			            if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
//			                System.out.println("\t…and the token is invalid as of " +
//			                    pushNotificationResponse.getTokenInvalidationTimestamp());
//			            }
//			        }
//				} catch (InterruptedException | ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//		        
//
//		} catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return new ResultObjecttInfo(Constants.OK, Constants.SUCCESS);
//	}
//	public static void main(String arg[]) {
//		ClientAppResource a = new ClientAppResource();
//		a.index();
//	}
}
