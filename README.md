# ont-auth

## 接口

### App

* [注册App](#注册App)
* [验证App](#验证App)

### DeviceCode

* [注册DeviceCode](#注册DeviceCode)
* [验证DeviceCode](#验证DeviceCode)

### JWT

* [注册JWT](#注册JWT)
* [验证JWT](#验证JWT)


### 健康检查

* [健康检查](#健康检查)


## 接口规范

### 注册App

```text
url：/v1/hmac
method：POST
```

- 请求：

```json
{
    "ontid":"did:ont:AUJf3fc2AebzxseyCbtPtLR1LSayoWL39S"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| ontid   | String |     |


- 响应：

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "appid": "Wa7i20md",
        "appkey": "VWFWdXFnDVKBaKW+3+rG1jPjKYRZcQ==",
        "ontid": "did:ont:AUJf3fc2AebzxseyCbtPtLR1LSayoWL39S"
    }
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |
| result     | String | 成功返回结果，失败返回null     |


### 验证App

```text
url：/v1/hmac/verification
method：POST
```

- 请求：

```json
{
    "hmac_data": "ont:4ktavTIs:5n05IJO22KVl7voUFm/L0fp/Pv+5dtQWmrGljJFr8aY=:OTNiOTZmYzYtNDg4ZS00ZTgxLWI1ZGItNzRhNDA4ZDA3ZWVl:1561100331",
    "method": "POST",
    "request_uri": "/api/v1/ontid/login/phone",
    "body_md5_base64_str": ""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
|    | String |     |


- 响应：

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": true
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |
| result     | String | 成功返回true，失败返回null     |


### 注册DeviceCode 

```text
url：/v1/device
method：POST
```

- 请求：

```json
{
    "ontid":"did:ont:AUJf3fc2AebzxseyCbtPtLR1LSayoWL39S",
    "signature":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| ontid   | String |     |
| signature   | String |   Base64编码  |


- 响应：

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "DeviceCode": "device74ec28cd8ada40a0bad66b17ec0ff5ec"
    }
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |
| result     | String | 成功返回结果，失败返回null     |


### 验证DeviceCode 

```text
url：/v1/device/verification
method：POST
```

- 请求：

```json
{
    "ontid":"",
    "devicecode":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| ontid   | String |     |
| devicecode   | String |     |


- 响应：

```json
{
    "result": "",
    "error": 0,
    "action": "verifyDevice",
    "version": "1.0.0",
    "desc": "SUCCESS"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |
| result     | String | 成功返回结果，失败返回null     |


### 注册JWT

```text
url：/v1/jwt
method：POST
```

- 请求：

```json
{
    "user_ontid":"did:ont:AUJf3fc2AebzxseyCbtPtLR1LSayoWL39S"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| dapp_ontid   | String |     |
| ontid   | String |     |


- 响应：

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJhdWQiOiJkaWQ6b250OkFRWnFWdlh5TUFWYUJpSjhjWWdqZkxZWGd2aEtEU0JNTksiLCJpc3MiOiJkaWQ6b250OkFkajdXNVoyaFRlS0g3WXdKc2ZNekx1d2lENjcxbXZKNlgiLCJleHAiOjE1NjA5MDIyMTYsImlhdCI6MTU2MDg0ODIxNiwianRpIjoiMGVkOTUxNTA3N2U0NGMyOTlhMTZkOWMyODA5YzMyMDUiLCJjb250ZW50Ijp7InR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJvbnRpZCI6ImRpZDpvbnQ6QVVKZjNmYzJBZWJ6eHNleUNidFB0TFIxTFNheW9XTDM5UyJ9fQ.MDExZDg0ZWEyNzA0ZjFlOTMzNGEzNTNjY2IzNjllMzA1ZDQxNWYxOWM1MDE4YmM0M2JkYjExN2NlOGFlYmY1OTg3MWE0MzZmNmEyM2I1YmI4NzMyMGQwZTQ1ODEzZWU2Mjg5ZTZiYzUzN2UxZDUzODVhMTNjMGUyNzVkMTg5YzFmYQ",
        "user_ontid": "did:ont:AUJf3fc2AebzxseyCbtPtLR1LSayoWL39S"
    }
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| result     | String | 成功返回，失败返回""     |
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |


### 验证JWT

```text
url：/v1/jwt/verification
method：POST
```

- 请求：

```json
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJhdWQiOiJkaWQ6b250OkFRWnFWdlh5TUFWYUJpSjhjWWdqZkxZWGd2aEtEU0JNTksiLCJpc3MiOiJkaWQ6b250OkFkajdXNVoyaFRlS0g3WXdKc2ZNekx1d2lENjcxbXZKNlgiLCJleHAiOjE1NjA5MDIyMTYsImlhdCI6MTU2MDg0ODIxNiwianRpIjoiMGVkOTUxNTA3N2U0NGMyOTlhMTZkOWMyODA5YzMyMDUiLCJjb250ZW50Ijp7InR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJvbnRpZCI6ImRpZDpvbnQ6QVVKZjNmYzJBZWJ6eHNleUNidFB0TFIxTFNheW9XTDM5UyJ9fQ.MDExZDg0ZWEyNzA0ZjFlOTMzNGEzNTNjY2IzNjllMzA1ZDQxNWYxOWM1MDE4YmM0M2JkYjExN2NlOGFlYmY1OTg3MWE0MzZmNmEyM2I1YmI4NzMyMGQwZTQ1ODEzZWU2Mjg5ZTZiYzUzN2UxZDUzODVhMTNjMGUyNzVkMTg5YzFmYQ"
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| access_token  | String |     |


- 响应：

```json
{
    "result": true,
    "code": 0,
    "msg": "SUCCESS"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| result     | String | 成功返回true，失败返回null     |
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |


### 健康检查

```text
url：/v1/check
method：GET
```

- 响应：

```json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": true
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| result     | String | 成功返回true，失败返回null     |
| code      | int    | 错误码                        |
| msg       | String | 成功返回SUCCESS，失败返回错误描述 |