package RequestsModule;

import RequestsModule.api.IRequestsController;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/Requests")
public class RequstsController implements IRequestsController {

    @RequestMapping(method = RequestMethod.POST, value = "/Registration", produces = "application/json")
    public void registrationRequests(ModelMap model, @RequestParam JSONObject json) {
        model.addAttribute("message", "Hello world!");
        System.out.print(json);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://localhost:9998/");
        server.start();

        System.out.println("Server running");
        System.out.println("Visit: http://localhost:9998/helloworld");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
   /* @RequestMapping(method = RequestMethod.POST, value = "/json")
    public
    @ResponseBody
    String parseJson(ModelMap model, @RequestParam String json) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObj);
        return jsonObj.toString();

    }

    @RequestMapping(method = RequestMethod.GET, value = "/person")
    public
    @ResponseBody
    Person parsePerson(@RequestParam String person) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(person);
            String name = jsonObj.getString("name");
            int age = Integer.parseInt(jsonObj.getString("age"));

            Person p = new Person();
            p.setName(name);
            p.setAge(age);
            ArrayList<String> bla = new ArrayList<String>();
            bla.add("bla1");
            bla.add("bla2");

            p.setKids(bla);
            //while (true){}
            return p;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObj);
        return null;

    }

    @RequestMapping(method = RequestMethod.GET, value = "/persons")
    public
    @ResponseBody
    List<Person> parsePerson() {
        ArrayList<Person> persons = new ArrayList<Person>();

        for (int i = 0; i < 5; i++) {
            Person p = new Person();
            p.setName("person" + i);
            p.setAge(20 + i);
            ArrayList<String> bla = new ArrayList<String>();
            bla.add("bla1");
            bla.add("bla2");
            persons.add(p);
        }


        return persons;

    }*/
}
