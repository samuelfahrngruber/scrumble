let changeSchema = require('../../data-layer/schemas/changeSchema');

module.exports = {
    change: (projectid, object, method, changeObject) => {
        let change = new changeSchema({
            timestamp: new Date().toISOString(),
            project: Number(projectid),
            object: object,
            method: method,
            change: changeObject
        });

        change.save((err) => {
            if (err) throw err;
        });
    }
};