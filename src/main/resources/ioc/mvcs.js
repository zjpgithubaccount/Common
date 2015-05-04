var ioc = {
    json: {
        type: "org.nutz.mvc.view.UTF8JsonView",
        args: [
            {
                type: 'org.nutz.json.JsonFormat',
                fields: {
                    autoUnicode: true
                }
            }
        ]
    }
}