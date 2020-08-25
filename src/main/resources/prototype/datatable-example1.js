
$(document).ready(function () {

    table = $('#example').DataTable({
        ajax: function (data, callback, settings) {
            $.ajax({
                type: "Post",
                url: "/Home/ShowData",
                cache: false,	//禁用缓存
                data: {
                    //组装分页参数
                    "PD.StartIndex": data.start,
                    "PD.PageSize": data.length,
                    "PD.Draw": data.draw,
                },
                dataType: "json",
                success: function (result) {
                    //封装返回数据
                    var returnData = {};
                    returnData.draw = result.Draw;
                    returnData.recordsTotal = result.RecordsTotal;//总记录数
                    returnData.recordsFiltered = result.RecordsFiltered;//后台不实现过滤功能，每次查询均视作全部结果
                    returnData.data = result.Data;
                    //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
                    //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
                    callback(returnData);
                },
                error: function (error) {
                    alert(error);
                }
            })
        },
        dom: 'Bfrtip',
        select: true,//单击行选中颜色凸显,此功能需要select插件
        serverSide: true,//开启服务端模式
        pageLength: 10,//每页默认最大显示行数
        columns: [
            { title: "编号", data: "UserID" },
            { title: "姓名", data: "UserName" },
            { title: "密码", data: "UserPwd" },
        ],
        "language": {
            url: dtsLanguage//中文配置文件地址
        }
    });
})