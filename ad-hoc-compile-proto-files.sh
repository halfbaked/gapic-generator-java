#!/bin/sh

set -o errexit

# Some auto-generated Java proto library code for .proto files are not published
# anywhere, so currently, we need to build them using `java_proto_library()` to
# generate .jar files.
#
# Most of these are needed only for compiling test source files.
#
# 1. from grpc/grpc-proto: service_config.proto
#
# 2. from googleapis/googleapis:
#    - gapic_metadata.proto
#    - common_resources.proto
#    - pubsub.proto (v2)
#    - logging.proto (v1)
#
# 3. local test resource .protos in src/test/resources:
#    - deprecated_service: deprecated_service.proto
#    - bookshop: bookshop.proto
#    - showcase: compliance.proto, echo.proto, identity.proto, testing.proto and messaging.proto
#    - testgapic: bad_message_resname_def.proto, locker.proto, and nested_message.proto
#    - explicit_dynamic_routing_headers_testing:
#       - routing_rule_parser_testing.proto
#       - explicit_dynamic_routing_header_testing.proto
#
#
# In addition to this, there is one special .proto in test resources that
# doesn't require compiled Java library but .descriptor generated by the
# protobuf copmiler: basic.proto. Currently, the Bazel target runs the
# following command to gerenrate this:
# protoc --include_source_info --include_imports --descriptor_set_out=basic_proto.descriptor basic.proto


bazel build :service_config_java_proto :test_java_protos :basic_proto_descriptor \
    @com_google_googleapis//gapic/metadata:metadata_java_proto \
    @com_google_googleapis//google/logging/v2:logging_java_proto \
    @com_google_googleapis//google/pubsub/v1:pubsub_java_proto

install_jar() {
  mvn install:install-file -Dfile=$1 -DgroupId=$2 -DartifactId=$3 -Dversion=0.0.0 -Dpackaging=jar
}

install_jar bazel-bin/external/io_grpc_proto/libservice_config_proto-speed.jar io.grpc serviceconfig-proto
install_jar bazel-bin/external/com_google_googleapis/gapic/metadata/libmetadata_proto-speed.jar com.google.api metadata-proto
install_jar bazel-bin/external/com_google_googleapis/google/cloud/libcommon_resources_proto-speed.jar com.google.api common-resources-proto
install_jar bazel-bin/external/com_google_googleapis/google/pubsub/v1/libpubsub_proto-speed.jar com.google.api pubsub-proto
install_jar bazel-bin/external/com_google_googleapis/google/logging/v2/liblogging_proto-speed.jar com.google.api logging-proto
install_jar bazel-bin/libtest_protos-speed.jar com.google.api gapic-generator-test-protos
