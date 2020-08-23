var aegis = {};

aegis.showErrors = function(errorData) {
    aegis.clearErrors();
    $.each(errorData, function (index, element) {
       var input = $("input[name=" + index + "]")[0];
       var appendDiv = $("div[name=" + index + "_append]")[0];
       if (input == null || input == {} || input == '') {
           input = $("select[name=" + index + "]")[0];
       }
       $(input).addClass("is-invalid");
       var divName = "errorArea_" + index;
       if (appendDiv != null) {
           $(appendDiv).after("<div class='invalid-feedback' name='"+ divName +"'>" + element + "</div>");
       } else {
           $(input).after("<div class='invalid-feedback' name='"+ divName +"'>" + element + "</div>");
       }
    });
}

aegis.clearErrors = function () {
    $("div[name^=errorArea_]").each(function (idx, ele) {
        $(ele).remove();
    });
    $("#mainForm").find("input").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
    $("#mainForm").find("select").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
}

aegis.required = function () {
    $("#mainForm").find("label").each(function (idx, ele) {
        if ($(ele).attr("data-required") == 'true') {
            var text = $(ele).html();
            text += "<span style='color: red'>&nbsp;*</span>";
            $(ele).html(text);
        }
    });
}
