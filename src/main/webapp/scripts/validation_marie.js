$(document).ready(function() {


  function resizePanels(){
    // Function to resize window depending on screen size
    var contentSourceSize = $(".entity-source .entity-inner-content").height();
    var contentTargetSize = $(".entity-target .entity-inner-content").height();
    var headerHeight = $("header").height();
    var footerHeight = $("footer").height();
    var contentWidth = $(".entities").width() - 30;
    var totalSize = $(window).height() - headerHeight - footerHeight;
    var halfSize =  Math.floor(totalSize/2);
    var newSourceSize = (contentSourceSize < halfSize) ? contentSourceSize : halfSize;
    var newTargetSize = (contentTargetSize < halfSize) ? contentTargetSize : halfSize;
    //console.log("contentSourceSize", contentSourceSize, "contentTargetSize", contentTargetSize, "totalSize", totalSize)
    //if()
    $(".entity-source").css({ "flexBasis" : newSourceSize+"px"});
    $(".entity-target").css({ "flexBasis" : newTargetSize+"px"});

    $("main aside").css({ "paddingTop" : headerHeight + "px", "paddingBottom" : footerHeight + "px" })

    $(".entity-source .entity-content").css({ "height" :$(".entity-source").height() +"px", "width" : contentWidth + "px" });
    $(".entity-target .entity-content").css({ "height" : $(".entity-target").height() +"px" , "width" : contentWidth + "px" });
  }
  
  // Button to switch between text and graph entities details
  $(".switch-nav").on("click", "li", function(){
    $(".entity-view").hide();
    $(this).find("button").addClass("btn-info");
    $(this).siblings("li").find("button").removeClass("btn-info");
    $(".entity-" + $(this).attr("class")).show();
  })
  
  $(window).on('resize', function(){
    resizePanels();
  })
  resizePanels();

});
