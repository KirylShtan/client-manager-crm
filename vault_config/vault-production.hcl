storage "file" {
  path = "/vault/file"
}
listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_disable   = false
  tls_cert_file = "/vault/tls/vault-production.crt"
  tls_key_file  = "/vault/tls/vault-production.key"
}
api_addr = "https://vault:8200"
ui = true