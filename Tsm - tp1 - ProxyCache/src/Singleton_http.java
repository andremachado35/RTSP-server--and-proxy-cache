import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Singleton_http {
    private Map<String,HttpResponse> Request_to_Response = new HashMap<String, HttpResponse>();
    private Map<String, DataOutputStream> Pedido_to_dataoutput = new HashMap<String, DataOutputStream>();

    private Map<HttpResponse, String> Response_to_Request = new HashMap <HttpResponse,String>();
    String pedido;
    private static Singleton_http instance;


    public static Singleton_http getInstance() {
        if (instance==null){
            instance = new Singleton_http();
        }
        return instance;

    }

    private Singleton_http() {
    }

    public void put_httprequest(String pedido_new){
        this.pedido = pedido_new;
    }

    public String get_httprequest (){
        return pedido;
    }

    public void add_Request_to_Response(String hr, HttpResponse hrs){
        Request_to_Response.put(hr,hrs);
    }

    public HttpResponse get_Response_by_httprequest (String hr){
        HttpResponse http_response_new = Request_to_Response.get(hr);
        return http_response_new;
    }

    public void add_Response_to_Request (HttpResponse hrs,String hr){
        Response_to_Request.put(hrs,hr);
    }

    public  String get_Reques_to_Response (HttpResponse hrs){
        String http_request_new = Response_to_Request.get(hrs);
        return http_request_new;
    }

    public  void add_Pedido_to_Data (String pe, DataOutputStream data) {
        Pedido_to_dataoutput.put(pe,data);
    }

    public DataOutputStream get_Data_by_pedido (String pe){
        DataOutputStream data_new = Pedido_to_dataoutput.get(pe);
        return data_new;
    }

}
