# Security Policy

## Supported Versions

| Version          | Supported          | GPG Signing Key  |
| ---------------- | ------------------ | ---------------- |
| 1.83             | :white_check_mark: | 31D2D79DF7E85DD3 |
| 1.82 and earlier | :x:                | ?                |

## Reporting a Vulnerability

If you find a security vulnerability, please [open a Github issue](https://github.com/cbeust/jcommander/issues).

We will try to publish a security fix on Maven Central ASAP after you reported it.

There will be no frequently scheduled security updates.

## GPG Signature Validation

All artefacts are published on the Maven Central Repository accompanied by an *.asc GPG signature file.

The GPG signing key used since v1.83 is found on [keyserver.ubunto.com](https://keyserver.ubuntu.com/pks/lookup?search=1D85469D8559C2E1DF5F925131D2D79DF7E85DD3&fingerprint=on&op=index):
```
pub   rsa3072 2023-08-04 [SC] [expires: 2025-08-03]
      1D85 469D 8559 C2E1 DF5F  9251 31D2 D79D F7E8 5DD3
uid           [ultimate] Markus KARG <markus@headcrashing.eu>
sub   rsa3072 2023-08-04 [E] [expires: 2025-08-03]
```
