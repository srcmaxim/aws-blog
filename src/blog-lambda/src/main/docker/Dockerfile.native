FROM public.ecr.aws/lambda/provided:al2 AS runtime
# Copy custom runtime bootstrap
COPY target/bootstrap-example.sh ${LAMBDA_RUNTIME_DIR}/bootstrap
# Copy function code
COPY target/*-runner ${LAMBDA_TASK_ROOT}/runner
# Setup bootstrap and runner
RUN chmod 775 ${LAMBDA_RUNTIME_DIR}/bootstrap
RUN chmod 775 ${LAMBDA_TASK_ROOT}/runner
# Setup Env
ENV DISABLE_SIGNAL_HANDLERS true

# Set the CMD to your handler
CMD [ "no.handler" ]
