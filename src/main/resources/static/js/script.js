console.log("this is js file")

const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

const  search= () => {
   
   let query = $("#search-input").val();
  
  if(query == "")
   {
	    console.log(query);
    $(".search-result").hide();
   }
   else{
    //console.log(query);
     let url= `http://localhost:8282/search/${query}`;
    fetch(url).then((Response) =>{
        return Response.json();
    }).then((data) => {
        console.log(data);
        let text= `<div class='list-group'>`;
        data.forEach((contact) => {
			text +=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-action'>${contact.name}</a>`
		});
        	text +=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        
    });
   
   }

};